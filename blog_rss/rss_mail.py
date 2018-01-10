# coding:utf-8

import smtplib
import urllib2
from bs4 import BeautifulSoup
from email.mime.text import MIMEText
from email.header import Header
import os
import sys
import json
import threading
reload(sys)
sys.setdefaultencoding('utf-8')



def send_mail(mail_user,server,subject,body,url):
    frm = qqmail_user
    to = 'qqnum@qq.com'

    i = 0
    j = 0
    for g in url:
        j = j+1
        if g == '/':
            i=i+1
        if i == 3:
            url = url[0:j]

    #发送内容
    msg = MIMEText(subject+"推出了最新的博客："+body+"。点击查看："+url, 'plain', 'utf-8')
    msg['From'] = frm
    msg['To'] = to
    msg['Subject'] = Header('更新博客提醒：'+subject, 'utf-8').encode()

    server.sendmail(frm, to,msg.as_string())
    print 'Email sent!'


def rss_gather(mail_user,url,server):

    while True:
        #发送请求爬，一般不会被正规网站反爬虫
        request = urllib2.Request(url)
        request.add_header('User-Agent','fake-client')
        html = urllib2.urlopen(request)
        #建立bs对象
        bsobj = BeautifulSoup(html,"html.parser")
        #查找所有title
        titles = bsobj.findAll("title")

        #建立循环找出前两个title，分别是blog名和最新更新
        i = 0
        subject = None
        body = None
        for title in titles:
            i = i+1
            title_str = str(title)
            if i == 1:
                print "blog名："+title_str[7:-8]
                name = title_str[7:-8]
            else:
                print "最新更新："+title_str[7:-8]
                body = title_str[7:-8]
                break
        if write_file(name,body,url) == 2:
            send_mail(mail_user,server,name,body,url)
        elif write_file(name,body,url) == 3:
            send_mail(mail_user,server,name,body,url)


def write_file(name,str,url):
    #判断是否在此文件夹下存在name文件
    if os.path.exists("./rss/"+name):
        fp = open("./rss/"+name,"r")
        rss_json = fp.read(300)

        print "new:"+rss_json
        jsonobj = json.loads(rss_json)
        #如果最新文章名没变
        if str == jsonobj.get("newest_article").encode("utf-8"):
            print "article not change:"+str
            return 1
        #如果str如文件中最新的文章名不同，重写文件并返回2
        else:
            #写入为json格式
            fp = open("./rss/"+name,"w")
            fp.write("{\"blog_name\":\""+name+"\",\"newest_article\":\""+str+"\","+"\"rss_url\":\""+url+"\"}")
            return 2
    #如果没有文件，创建文件并返回3
    else:
        fp = open("./rss/"+name,"w")
        fp.write("{\"blog_name\":\""+name+"\",\"newest_article\":\""+str+"\","+"\"rss_url\":\""+url+"\"}")
        return 3




if __name__ == '__main__':

    #初始化邮箱
    qqmail_user = 'qqnum@qq.com'
    qqmail_passwd = '授权玛'

    try:
        server = smtplib.SMTP_SSL("smtp.qq.com",465)
        #server.ehlo()
        #server.set_debuglevel(1)
        print "connection mail server ok!"

        server.login(qqmail_user,qqmail_passwd)
        print "login qqmail ok!"

    except Exception,e:
        server.close()
        #socket_server.close()
        print "init_qqmail error!"+str(e)

    rsss=["https://ikk.me/feed/"]
    rsss.append("https://blog.yoitsu.moe/feeds/all.atom.xml")
    rsss.append("https://farseerfc.me/feeds/atom.xml")
    rsss.append("https://blog.phoenixlzx.com/atom.xml")
    rsss.append("https://huihui.moe/feed")
    rsss.append("https://ikk.me/feed/")
    '''
    while True:
        for rss in rsss:
            rss_gather(qqmail_user,rss,server)
        '''

    fp = open("rss_list","r")

    while 1:
        rss = fp.readline()
        if not rss:
            break;
        t = threading.Thread(target = rss_gather,args = (qqmail_user,rss,server,))
        t.start()
