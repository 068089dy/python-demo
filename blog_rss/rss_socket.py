# coding:utf-8
import socket
import threading

#找出更新的文件并返回文件名，若没有更新则返回'hehe',会在当前目录兴建“rss2”文件夹以存放备份文件
def find_new_file():

    if os.path.exists("./rss/"):
        if os.path.exists("./rss2/"):
            fileNames = os.listdir("./rss/")
            for fileName in fileNames:
                if os.path.exists("./rss2/"+fileName+"2"):
                    fp1 = open("./rss/"+fileName,"r")
                    str1 = fp1.read()
                    fp2 = open("./rss2/"+fileName+"2","r")
                    str2 = fp2.read()
                    if str1 == str2:
                        rtv = "hehe"
                    else:
                        fp = open("./rss2/"+fileName+"2","w")
                        fp.write(str1)
                        rtv = fileName
                else:
                    fp = open("./rss2/"+fileName+"2","w")
                    fp1 = open("./rss/"+fileName,"r")
                    str1 = fp1.read()
                    fp2 = open("./rss2/"+fileName+"2","r")
                    str2 = fp2.read()
                    if str1 == str2:
                        rtv = "hehe"
                    else:
                        fp = open("./rss2/"+fileName+"2","w")
                        fp.write(str1)
                        rtv = fileName
            return rtv
        else:
            os.mkdir("./rss2/")
            fileNames = os.listdir("./rss/")
            for fileName in fileNames:
                if os.path.exists("./rss2/"+fileName+"2"):
                    fp1 = open("./rss/"+fileName,"r")
                    str1 = fp1.read()
                    fp2 = open("./rss2/"+fileName+"2","r")
                    str2 = fp2.read()
                    if str1 == str2:
                        rtv = "hehe"
                    else:
                        fp = open("./rss2/"+fileName+"2","w")
                        fp.write(str1)
                        rtv = fileName
                else:
                    fp = open("./rss2/"+fileName+"2","w")
                    fp1 = open("./rss/"+fileName,"r")
                    str1 = fp1.read()
                    fp2 = open("./rss2/"+fileName+"2","r")
                    str2 = fp2.read()
                    if str1 == str2:
                        rtv = "hehe"
                    else:
                        fp = open("./rss2/"+fileName+"2","w")
                        fp.write(str1)
                        rtv = fileName
            return rtv
    else:
        return "hehe"



def socket_thread(client_socket):
    print "socket——————————————run"
    request = client_socket.recv(1024)
    print "接受到"+request
    #刷新列表
    if len(str(request) or "refresh") == 7:
        send_name = find_new_file()

        if send_name != "hehe":
            fp = open("./rss/"+send_name)
            send = fp.read(200)
            client_socket.send(send)

        else:
            client_socket.send("hehe")
    #添加rss
    elif len(str(request) or "addrss") == 6:
        print "addrss"
        client_socket.send("add ok!")

    print "socket send finish!"
    client_socket.close()



bind_ip = "localhost"
bind_port = 8887

server = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
server.bind((bind_ip,bind_port))
server.listen(5)
print "server start:",bind_port

while True:

    client,addr = server.accept()
    client_handler = threading.Thread(target=socket_thread,args=(client,))
    client_handler.start()
