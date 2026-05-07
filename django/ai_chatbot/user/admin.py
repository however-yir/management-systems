from django.contrib import admin
from import_export.admin import ImportExportActionModelAdmin

from .models import User
class UserAdmin(ImportExportActionModelAdmin):
    # 设置模型字段，用于Admin后台数据的表头设置
    list_display = ['id', 'name', 'password','nickname', 'description','phone','create_time']
    # 设置可搜索的字段并在Admin后台数据生成搜索框，如有外键应使用双下划线连接两个模型的字段
    search_fields = ['id', 'nickname']
    # 设置过滤器，在后台数据的右侧生成导航栏，如有外键应使用双下划线连接两个模型的字段
    list_filter = ['id', 'nickname']
    list_display_links = ['name']
    # 设置排序方式
    ordering = ['id']


admin.site.register(User, UserAdmin)


admin.site.site_header = '基于深度学习的聊天机器人系统'
admin.site.site_title = '基于深度学习的聊天机器人系统'
admin.site.index_title = '4'