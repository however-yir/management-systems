from django.contrib import admin
from django.urls import path, include
from . import views

urlpatterns = [
    path('', views.login),
    path('index', views.index),
    path('personal', views.personal),
    path('login_out', views.login_out),
    path('get_data',views.get_data),
    path('data',views.data),
    path('del_data',views.del_data),
    path('edit_data',views.edit_data),
    path('register',views.register),
    path('add_info',views.add_info),
]
