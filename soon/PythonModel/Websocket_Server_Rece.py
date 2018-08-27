from bottle import request, Bottle, abort
from geventwebsocket import WebSocketError
from gevent.pywsgi import WSGIServer
from geventwebsocket.handler import WebSocketHandler
from xmlProcessor import analyzeXml_dict
from ReadFile import MD5_Value
app = Bottle()


@app.get('/websocket')
def handle_websocket():
    global i
    i += 1
    path = '/home/mahaoli/data' + str(i) + '.xml'
    print('第' + str(i) + '次调用方法')
    wsock = request.environ.get('wsgi.websocket')
    if not wsock:
        abort(400, 'Expected WebSocket request.')
    while True:
        try:
            message = wsock.receive()
            if message:
                with open(path, "w", newline='\n') as f:
                    f.write(message)
                    f.close()
        except WebSocketError:
            break
        else:
            if i == 2:
                md5 = analyzeXml_dict(path)['MD5_value']
                print(md5)
                if md5 == MD5_Value('/home/data.xml'):
                    wsock.send('The file received is true')
                else:
                    wsock.send('The file received is false')
            if i == 4:
                md6 = analyzeXml_dict(path)['MD5_value']
                print(md6)
                if md6 == MD5_Value('/home/model.xml'):
                    wsock.send('The file received is true')
                else:
                    wsock.send('The file received is false')

    return True


if __name__ == '__main__':
    i = 0
    server = WSGIServer(("localhost", 5001), app, handler_class=WebSocketHandler)
    server.serve_forever()
