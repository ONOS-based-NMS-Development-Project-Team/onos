import tensorflow as tf


class NeurosNetwork(object):

    def __init__(self,input_layer_neurosNums,output_layer_neurosNums,hidden_layer_nums,
                 leraning_rate,epoch,activation_function = tf.nn.relu,hidden_layer_neurosNums=[]):
        self.input_layer_neurosNums = input_layer_neurosNums
        self.output_layer_neurosNums = output_layer_neurosNums
        self.hidden_layer_nums = hidden_layer_nums
        self.learingrate = leraning_rate
        self.epoch = epoch
        self.activation_function = activation_function
        self.hidden_layer_neurosNums = hidden_layer_neurosNums
        network_weights = self._initialize_weights()
        self.weights = network_weights
        # 网络结构
        self.x = tf.placeholder(tf.float32, [None,self.input_layer_neurosNums])
        self.y = tf.placeholder(tf.float32, [None,self.output_layer_neurosNums])
        self.hidden = self._layer()
        # 最后一层数据复原不再需要激活函数
        self.reconstruction = self._output_Layer()
        with tf.name_scope('loss'):
            # self.loss = tf.reduce_mean(tf.reduce_sum(tf.square(self.y - self.reconstruction), reduction_indices=[1]))
            diff = tf.nn.softmax_cross_entropy_with_logits(logits=self.y, labels=self.reconstruction)
            with tf.name_scope('total'):
                self.loss = tf.reduce_mean(diff)
        tf.summary.scalar('loss', self.loss)
        with tf.name_scope('train'):
            self.optimizer = tf.train.GradientDescentOptimizer(self.learingrate).minimize(self.loss)
        init = tf.global_variables_initializer()
        self.merged = tf.summary.merge_all()
        self.sess = tf.Session()
        self.sess.run(init)
        # self.sess.run(merged)

    def _initialize_weights(self):
        all_weights = dict()
        n = self.hidden_layer_nums + 1
        all_weights['w1'] = tf.Variable(tf.truncated_normal([self.input_layer_neurosNums,self.hidden_layer_neurosNums[0]],stddev=0.1, dtype = tf.float32))
        all_weights['b1'] = tf.Variable(tf.zeros([self.hidden_layer_neurosNums[0]], dtype = tf.float32))
        for i in range(2, n+1):
            if i == n:
                all_weights['w' + str(i)] = tf.Variable(tf.truncated_normal([self.hidden_layer_neurosNums[i-2], self.output_layer_neurosNums],stddev=0.1 , dtype=tf.float32))
                all_weights['b' + str(i)] = tf.Variable(tf.zeros([self.output_layer_neurosNums], dtype=tf.float32))
                break
            all_weights['w' + str(i)] = tf.Variable(tf.truncated_normal([self.hidden_layer_neurosNums[i-2],self.hidden_layer_neurosNums[i-1]],stddev=0.1))
            all_weights['b' + str(i)] = tf.Variable(tf.zeros([self.hidden_layer_neurosNums[i-1]],dtype = tf.float32))
        return all_weights

    def _layer(self):
        with tf.name_scope('layer1'):
            with tf.name_scope('w1'):
                self.variable_summaries(self.weights['w1'])
            with tf.name_scope('b1'):
                self.variable_summaries(self.weights['b1'])
            with tf.name_scope('Wx_plus_b'):
                y = tf.add(tf.matmul(self.x, self.weights['w1']), self.weights['b1'])
                tf.summary.histogram('y', y)
            out = self.activation_function(y, name='out')
            tf.summary.histogram('out', out)
        if self.hidden_layer_nums == 1:
            return out
        else:
            for j in range(2, self.hidden_layer_nums + 1):
                print(j)
                with tf.name_scope('layer' + str(j)):
                    with tf.name_scope('w' + str(j)):
                        self.variable_summaries(self.weights['w' + str(j)])
                    with tf.name_scope('b' + str(j)):
                        self.variable_summaries(self.weights['b' + str(j)])
                    with tf.name_scope('Wx_plus_b'):
                        y = tf.add(tf.matmul(out, self.weights['w'+str(j)]), self.weights['b'+str(j)])
                        tf.summary.histogram('y', y)
                    out = self.activation_function(y, name='out')
                    tf.summary.histogram('out', out)
            return out

    # 输出层
    def _output_Layer(self):
        l = self.hidden_layer_nums+1
        with tf.name_scope('Outputlayer'):
            with tf.name_scope('w' + str(l)):
                self.variable_summaries(self.weights['w' + str(l)])
            with tf.name_scope('b' + str(l)):
                self.variable_summaries(self.weights['b' + str(l)])
            with tf.name_scope('Wx_plus_b'):
                y = tf.add(tf.matmul(self.hidden, self.weights['w' + str(l)]), self.weights['b' + str(l)])
                tf.summary.histogram('y', y)
            result = tf.identity(y, name='result')
            tf.summary.histogram('result', result)
            return result



    # 用一条batch数据进行训练并返回当前损失loss
    def partial_fit(self, X, Y):
        loss, opt = self.sess.run([self.loss, self.optimizer], feed_dict = {self.x:X, self.y:Y})
        return loss

    # 训练完毕进行测试
    def verify(self, X):
        return self.sess.run(self.reconstruction, feed_dict={self.x:X})

    # 获取训练后的参数
    def getWeights(self):
        return self.sess.run(self.weights)

    # 参数值统计
    def variable_summaries(self, var):
        with tf.name_scope('summaries'):
            mean = tf.reduce_mean(var)
            tf.summary.scalar('mean', mean)
            with tf.name_scope('stddev'):
                stddev = tf.sqrt(tf.reduce_mean(tf.square(var - mean)))
            tf.summary.scalar('stddev', stddev)
            tf.summary.scalar('max', tf.reduce_max(var))
            tf.summary.scalar('min', tf.reduce_min(var))
            tf.summary.histogram('histogram', var)

    # 训练过程
    def traingResult(self, X, Y):
        train_writter = tf.summary.FileWriter('/home/mahaoli/anaconda3/envs/ttt/Project', self.sess.graph)
        for i in range(self.epoch):
            value, opt = self.sess.run([self.merged, self.optimizer], feed_dict={self.x: X, self.y: Y})
            if i % 10 == 0:
                # print(i, self.sess.run(self.loss, feed_dict={self.x: X, self.y: Y}))
                train_writter.add_summary(value, i)
            if i % 100 == 0:
                print(i, self.sess.run(self.loss, feed_dict={self.x: X, self.y: Y}))