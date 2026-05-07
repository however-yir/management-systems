import datetime


def day_get():
    d = datetime.datetime.now()
    # 通过for 循环得到天数，如果想得到两周的时间，只需要把8改成15就可以了。
    for i in range(1, 8):
        oneday = datetime.timedelta(days=i)
        day = d - oneday
        date_to = datetime.datetime(day.year, day.month, day.day)
        yield str(date_to)[0:10]


def get_recent_seven_day():
    """
    获取最近的七天日期
    @return:
    """
    qq = day_get()
    day_list = []
    for obj in qq:
        day_list.append(obj)
    list_week_day = day_list[::-1]
    return list_week_day
