from django.db import models


class Info(models.Model):
    id = models.AutoField(primary_key=True)
    question = models.CharField(verbose_name="图片名", default='', max_length=100)
    answer = models.CharField(verbose_name="结果", default='', max_length=100)
    status = models.CharField(verbose_name="状态", default='', max_length=100)
    owner = models.CharField(verbose_name="操作人", default='', max_length=100)
    create_time = models.DateTimeField('创建时间', auto_now_add=True)

    def __str__(self):
        return self.question

    class Meta:
        db_table = 'info'