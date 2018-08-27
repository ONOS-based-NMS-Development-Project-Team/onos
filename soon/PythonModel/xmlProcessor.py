#bookstore.py
#encoding:utf-8
'''
根据一个给定的XML Schema，使用DOM树的形式从空白文件生成一个XML。
'''
from xml.dom import minidom
from xml.dom.minidom import Document
import xml.dom.minidom
import xml.etree.ElementTree as ET

"""
生成xml文件：由list生成xml文件
@:param dataset : 数据集
@:param filepath : 生成xml文件保存的路径，若只输入文件名，则与此文件在同一目录下
@:return xml文件
"""
def generate_xml_list_dict(datas:list, filepath:str):
    doc = Document()  #创建DOM文档对象
    dataset = doc.createElement('dataset') #创建根元素
    # dataset.setAttribute('xmlns:xsi', "http://www.w3.org/2001/XMLSchema")#设置命名空间
    # dataset.setAttribute('xsi:noNamespaceSchemaLocation', 'dataSet.xsd')#引用本地XML Schema
    doc.appendChild(dataset)
    # Python处理XML之Minidom#
    i = 0
    for oneData in datas[:]:
        data = doc.createElement('data')
        data.setAttribute('id', str(i))
        dataset.appendChild(data)
        i = i + 1
        for key in oneData:
            Object = doc.createElement(str(key))
            Object_text = doc.createTextNode(str(oneData[key])) #元素内容写入
            Object.appendChild(Object_text)
            data.appendChild(Object)
    #将DOM对象doc写入文件
    f = open(filepath, 'w')
    f.write(doc.toprettyxml(indent=''))
    f.close()
    # return filepath

"""
生成xml文件：由dict生成xml文件
@:param dataset : 模型参数
@:param filepath : 生成xml文件保存的路径，若只输入文件名，则与此文件在同一目录下
@:return xml文件
"""
def generate_xml_dict(input:dict, filepath:str):
    doc = Document()  #创建DOM文档对象
    dataset = doc.createElement('param') #创建根元素
    # dataset.setAttribute('xmlns:xsi', "http://www.w3.org/2001/XMLSchema")#设置命名空间
    # dataset.setAttribute('xsi:noNamespaceSchemaLocation', 'dataSet.xsd')#引用本地XML Schema
    doc.appendChild(dataset)
    #Python处理XML之Minidom#
    for key in input:
        Object = doc.createElement(str(key))
        Object_text = doc.createTextNode(str(input[key])) #元素内容写入
        Object.appendChild(Object_text)
        dataset.appendChild(Object)
    #将DOM对象doc写入文件
    f = open(filepath, 'w')
    f.write(doc.toprettyxml(indent=''))
    f.close()
    return filepath

"""
解析xml文件：解析xml文件生成list
@:param filepath : 生成xml文件保存的路径，若只输入文件名，则与此文件在同一目录下
@:return dict
"""
def analyzeXml_dict(filename):

    # fa1
    '''
    aa = xml.dom.minidom.parse(filename)
    # 获取根节点
    rootElement = aa.documentElement
    # 得到根节点下面所有的element节点
    # 更多方法可以参考w2school的内容或者用dir(root)获取
    save = {}
    for ele in rootElement.childNodes:
        save[ele.nodeName] = ele.nodeValue
    print(save)
    '''

    # fa2
    tree = ET.ElementTree(file=filename)
    root = tree.getroot()
    element = {}
    i = 0
    for child_of_root in root:
        element[child_of_root.tag] = child_of_root.text
        i += 1
    return element

    """
    subElement = {}
    allElement = {}
    for child_of_root in root:
        subElement[child_of_root.tag] = child_of_root.text
    print(subElement)
    allElement[root] = subElement
    print(allElement)
    """

"""
解析xml文件：解析xml文件生成list
@:param filepath : 生成xml文件保存的路径，若只输入文件名，则与此文件在同一目录下
@:return list
"""
def analyzeXml_list(filename):

    tree = ET.ElementTree(file=filename)
    root = tree.getroot()
    allElement = []
    for child in root:
        subElement = {}
        for child_of_child in child:
            subElement[child_of_child.tag] = child_of_child.text
        allElement.append(subElement)
    return allElement

if __name__ == '__main__':
    #dict = analyzeXml_list("E:/data.xml")
    #print(len(dict))
    #print(dict)
    list = analyzeXml_dict('/home/mahaoli/CodeHouse/Apptest/model.xml')
    print(list)