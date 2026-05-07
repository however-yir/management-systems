from django.db import models


class Role(models.Model):
    """
    角色表
    """
    id = models.AutoField(primary_key=True)
    name = models.CharField(verbose_name='角色名称', default='', max_length=50)
    abbreviation = models.CharField(max_length=32, blank=True, null=True, verbose_name="缩写")
    remark = models.CharField(max_length=128, blank=True, null=True, verbose_name="备注")
    order = models.IntegerField(blank=True, null=True, verbose_name="排序号")
    create_time = models.DateTimeField(verbose_name='创建时间', auto_now_add=True)

    def __str__(self):
        return self.name

    class Meta:
        db_table = 'role'


class User(models.Model):
    """
    用户表
    """
    id = models.AutoField(primary_key=True)
    name = models.CharField(verbose_name='姓名', default='', max_length=50)
    password = models.CharField(verbose_name='密码', default='123', max_length=50)
    nickname = models.CharField(verbose_name='昵称', default='1', max_length=50)
    phone = models.CharField(verbose_name='手机', default='15953654789', max_length=11)
    create_time = models.DateTimeField(verbose_name='创建时间', auto_now_add=True)
    modify_time = models.DateTimeField(verbose_name='最后修改时间', auto_now=True)
    role = models.ForeignKey(Role, verbose_name='角色', on_delete=models.CASCADE)
    description = models.TextField('个人描述')

    def __str__(self):
        return self.name

    class Meta:
        db_table = 'user'
