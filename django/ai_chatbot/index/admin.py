from django.contrib import admin

# Register your models here.
from import_export.admin import ImportExportActionModelAdmin

from .models import Info

class InfoAdmin(ImportExportActionModelAdmin):
    # 设置模型字段，用于Admin后台数据的表头设置
    list_display = ['id', 'question', 'answer','owner', 'status','create_time']
    # 设置可搜索的字段并在Admin后台数据生成搜索框，如有外键应使用双下划线连接两个模型的字段
    search_fields = ['id', 'owner']
    # 设置过滤器，在后台数据的右侧生成导航栏，如有外键应使用双下划线连接两个模型的字段
    list_filter = ['id', 'owner']
    list_display_links = ['question']
    # 设置排序方式
    ordering = ['id']


admin.site.register(Info, InfoAdmin)