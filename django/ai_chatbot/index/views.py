import datetime
import json
import os
from django.core.paginator import Paginator
from django.db.models import Count
from django.http import HttpResponseRedirect, HttpResponse, HttpResponseForbidden, JsonResponse
from django.shortcuts import render
from user.models import User
from utils import common
from .models import *

workdir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def login(request):
    """
    跳转登录
    :param request:
    :return:
    """
    return render(request, 'login.html')


def register(request):
    """
    跳转注册
    :param request:
    :return:
    """
    return render(request, 'reg.html')


def index(request):
    """
    跳转首页
    :param request:
    :return:
    """
    username = request.session.get('username', 'admin')
    role = int(request.session.get('role', 3))
    user_id = request.session.get('user_id', 1)
    total_user = len(User.objects.all())
    date = datetime.datetime.today()
    month = date.month
    year = date.year
    day = date.day
    current_day = "{}-{}-{}".format(year, month, day)
    # 获取近七天的操作
    seven_days = common.get_recent_seven_day()
    seven_data_dict = dict.fromkeys(seven_days, 0)
    for i in seven_days:
        d = datetime.timedelta
        r = Info.objects.filter(create_time__year=i.split('-')[0], create_time__month=i.split('-')[1],
                                create_time__day=i.split('-')[2]).all()
        if r:
            seven_data_dict[i] = r.count()
    seven_count_list = [seven_data_dict[x] for x in seven_days]
    return render(request, 'index.html', locals())


def login_out(request):
    """
    注销登录
    :param request:
    :return:
    """
    return HttpResponseRedirect('/')


def personal(request):
    username = request.session['username']
    role_id = request.session['role']
    user = User.objects.filter(name=username).first()

    return render(request, 'personal.html', locals())


def add_info(request):
    r = json.loads(request.body.decode().replace("'", "\""))
    question = r.get('question')
    text = r.get('answer')
    r = ''
    try:
        from detection.src.web import main
        r = main(question)

    except Exception as e:
        pass
    Info.objects.create(
        question=question,
        answer=text,
        status='成功',
        owner='admin'
    )
    return JsonResponse({'msg': 'ok', 'result': r})


def get_data(request):
    """
    获取列表信息 | 模糊查询
    :param request:
    :return:
    """
    keyword = request.GET.get('name')
    page = request.GET.get("page", '')
    limit = request.GET.get("limit", '')
    response_data = {}
    response_data['code'] = 0
    response_data['msg'] = ''
    data = []
    if keyword is None:
        results_obj = User.objects.all()
    else:
        results_obj = User.objects.filter(name__contains=keyword).all()
    paginator = Paginator(results_obj, limit)
    results = paginator.page(page)
    if results:
        for user in results:
            record = {
                "id": user.id,
                "name": user.name,
                "password": user.password,
                "phone": user.phone,
                "role": user.role,
                'create_time': user.create_time.strftime('%Y-%m-%d %H:%m:%S'),
                "desc": user.description,
            }
            data.append(record)
        response_data['count'] = len(results_obj)
        response_data['data'] = data

    return JsonResponse(response_data)


def data(request):
    """
    跳转页面
    """
    username = request.session.get('username', '')
    role = int(request.session.get('role', 3))
    user_id = request.session.get('user_id', 1)
    return render(request, 'data.html', locals())


def edit_data(request):
    """
    修改信息
    """
    response_data = {}
    user_id = request.POST.get('id')
    username = request.POST.get('username')
    phone = request.POST.get('phone')
    User.objects.filter(id=user_id).update(
        name=username,
        phone=phone)
    response_data['msg'] = 'success'
    return JsonResponse(response_data, status=201)


def del_data(request):
    """
    删除信息
    """
    user_id = request.POST.get('id')
    result = User.objects.filter(id=user_id).first()
    try:
        if not result:
            response_data = {'error': '删除失败！', 'message': '找不到id为%s' % user_id}
            return JsonResponse(response_data, status=403)
        result.delete()
        response_data = {'message': '删除成功！'}
        return JsonResponse(response_data, status=201)
    except Exception as e:
        response_data = {'message': '删除失败！'}
        return JsonResponse(response_data, status=403)


def save_file(file):
    """
    保存文件
    """
    if file is not None:
        file_dir = os.path.join(workdir, 'static', 'uploadImg')
        if not os.path.exists(file_dir):
            os.mkdir(file_dir)
        file_name = os.path.join(file_dir, file.name)
        with open(file_name, 'wb')as f:
            # chunks()每次读取数据默认 我64k
            for chunk in file.chunks():
                f.write(chunk)
            f.close()
        return file_name
    else:
        return None
