import json
import time

from django.shortcuts import render

from .models import User
from django.http import JsonResponse, HttpResponseRedirect
from django.core.paginator import Paginator


# from utils.mypage import Pagination


def register(request):
    """
    注册账号
    :return:
    """
    r = json.loads(request.body.decode().replace("'", "\""))
    name = r.get('username')
    passwd =r.get('password')
    nickname = r.get('nickname')
    #name = request.POST.get('username')
    #passwd = request.POST.get('password')
    phone = request.POST.get('phone','123')
    #nickname = request.POST.get('nickname')
    role = request.POST.get('role',2)
    user_obj = User.objects.filter(name=name).first()
    if user_obj:
        return JsonResponse({'message': '用户已存在,请直接登录'}, status=403)
    User.objects.create(
        name=name,
        password=passwd,
        phone=phone,
        nickname=nickname,
        role_id=role,
        description=''
    )
    response_data = {'msg': '注册成功!'}
    return JsonResponse(response_data)



def password(request):
    username = request.session.get('username','')
    role = int(request.session.get('role',3))
    user_id = request.session.get('user_id',1)
    return render(request, 'modify_password.html', locals())


def get_user(request):
    """
    获取用户列表信息 | 模糊查询
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
        for result in results:
            record = {
                "id": result.id,
                "name": result.name,
                "password": result.password,
                "email": result.email,
                "phone": result.phone,
                "role": result.role.name,
                'create_time': result.create_time.strftime('%Y-%m-%d %H:%m:%S'),
                "desc": result.description,
            }
            data.append(record)
        response_data['count'] = len(results_obj)
        response_data['data'] = data

    return JsonResponse(response_data)


def user(request):
    """
    跳转用户页面
    """
    username = request.session.get('username','')
    role = int(request.session.get('role',3))
    user_id = request.session.get('user_id',1)
    return render(request, 'user.html', locals())


def login_check(request):
    """
    登录校验
    """
    response_data = {}
    name = request.POST.get('username')
    password = request.POST.get('password')
    user_obj = User.objects.filter(name=name, password=password).first()
    if user:
        # 将用户名存入session中
        request.session["username"] = user_obj.name
        request.session["role"] = user_obj.role.id
        request.session["user_id"] = user_obj.id
        response_data['msg'] = '登录成功'
        return JsonResponse(response_data, status=201)
    else:
        return JsonResponse({'message': '用户名或者密码不正确'}, status=401)


def edit_user(request):
    """
    修改用户
    """
    response_data = {}
    user_id = request.POST.get('id')
    username = request.POST.get('username', '')
    phone = request.POST.get('phone', '')
    User.objects.filter(id=user_id).update(
        name=username,
        phone=phone)
    response_data['msg'] = '编辑成功！'
    response_data['error'] = 0
    return JsonResponse(response_data, status=201)


def del_user(request):
    """
    删除用户
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


def change_password(request):
    """
    修改密码
    """

    user_obj = User.objects.filter(name=request.session["username"]).first()
    password1 = request.POST.get('password2')
    password2 = request.POST.get('password2')
    if password1 != user_obj.password:
        return JsonResponse({"msg": "原密码不正确！"}), 405
    if user_obj.password == request.POST.get('password2'):
        # 修改的密码与原密码重复不予修改
        return JsonResponse({"msg": "修改密码与原密码重复"}), 406
    else:
        # 不重复，予以修改
        User.objects.filter(name=request.session["username"]).update(
            password=password2)
        # 清除session回到login界面
        del request.session['username']
        return JsonResponse({"msg": "success"})
