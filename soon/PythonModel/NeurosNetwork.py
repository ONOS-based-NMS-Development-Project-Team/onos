import tensorflow as tf
import os
import threading
import subprocess
import socket


class NeuroNetwork(object):

    def __init__(self, input_layer_neurosNums, output_layer_neurosNums, hidden_layer_nums,
                 leraning_rate, epoch, batch_size, optimizerFunction,
                 lrAdjust, activation_function, dropout=0.0,
                 lossFunction = tf.reduce_mean, hidden_layer_neurosNums=[]):
        # self.saver = tf.train.Saver()
        self.logdir = os.environ['HOME'] + '/'+threading.current_thread().name
        self.input_layer_neurosNums = input_layer_neurosNums
        self.output_layer_neurosNums = output_layer_neurosNums
        self.hidden_layer_nums = hidden_layer_nums
        self.batch_size = batch_size
        self.lossFunction = lossFunction
        self.optimizerFunction = optimizerFunction
        self.lrAdjust = lrAdjust
        self.dropout = dropout
        self.global_step = tf.Variable(0)
        self.initial_learning_rate = leraning_rate
        self.epoch = epoch
        self.learning_rate = tf.train.exponential_decay(self.initial_learning_rate, global_step=self.global_step,
                                                        decay_steps=self.epoch/self.batch_size,decay_rate=0.8)
        self.activation_function = activation_function
        self.hidden_layer_neurosNums = hidden_layer_neurosNums
        network_weights = self._initialize_weights()
        self.weights = network_weights
        # network structure
        self.x = tf.placeholder(tf.float32, [None,self.input_layer_neurosNums])
        self.y = tf.placeholder(tf.float32, [None,self.output_layer_neurosNums])
        self.hidden = self._layer()
        # last layer without activationFunction
        self.reconstruction = self._output_Layer()
        with tf.name_scope('loss'):
            if self.lossFunction == tf.reduce_mean:
                diff = tf.reduce_sum(tf.square(self.y - self.reconstruction), reduction_indices=[1])
            elif self.lossFunction == tf.nn.softmax_cross_entropy_with_logits:
                diff = tf.reduce_sum(tf.nn.softmax_cross_entropy_with_logits_v2(logits=self.reconstruction, labels=self.y))
            elif self.lossFunction == tf.abs:
                diff = tf.reduce_sum(tf.abs(self.reconstruction - self.y))
            with tf.name_scope('total'):
                self.loss = tf.reduce_mean(diff)
        tf.summary.scalar('loss', self.loss)
        with tf.name_scope('train'):
            if self.lrAdjust == tf.train.exponential_decay:
                self.optimizer = self.optimizerFunction(self.learning_rate).minimize(self.loss)
            else:
                self.optimizer = self.optimizerFunction(self.initial_learning_rate).minimize(self.loss)
        with tf.name_scope('accuracy'):
            with tf.name_scope('correct_prediction'):
                correct_prediction = tf.equal(tf.argmax(self.y, 1), tf.argmax(self.reconstruction, 1))
            with tf.name_scope('accuracy'):
                self.accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
        tf.summary.scalar('accuracy', self.accuracy)
        init = tf.global_variables_initializer()
        self.merged = tf.summary.merge_all()
        self.sess = tf.InteractiveSession()
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
                return all_weights
            all_weights['w' + str(i)] = tf.Variable(tf.truncated_normal([self.hidden_layer_neurosNums[i-2], self.hidden_layer_neurosNums[i-1]], stddev=0.1))
            all_weights['b' + str(i)] = tf.Variable(tf.zeros([self.hidden_layer_neurosNums[i-1]], dtype=tf.float32))
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



    # one batch return current loss
    def partial_fit(self, X, Y):
        loss, opt = self.sess.run([self.loss, self.optimizer], feed_dict = {self.x:X, self.y:Y})
        return loss

    # train end verify
    def verify(self, X):
        return self.sess.run(self.reconstruction, feed_dict={self.x:X})

    # get parameter
    def getWeights(self):
        return self.sess.run(self.weights)


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

    # training process
    def traingResult(self, X, Y, i):
        train_writter = tf.summary.FileWriter(self.logdir + '/train' + str(i), self.sess.graph)
        loss_list = []
        for i in range(self.epoch+1):
            value, opt = self.sess.run([self.merged, self.optimizer], feed_dict={self.x: X, self.y: Y})
            if i % 10 == 0:
                # print(i, self.sess.run(self.loss, feed_dict={self.x: X, self.y: Y}))
                train_writter.add_summary(value, i)
                # self.saver.save(self.sess, self.logdir+'/model.ckpt',i)
            if i % 100 == 0:
                loss = self.sess.run(self.loss, feed_dict={self.x: X, self.y: Y})
                print(i, loss)
                loss_list.append(loss)
        train_writter.close()
        return loss_list

    # testing process
    def testResult(self, X, Y):
        test_writter = tf.summary.FileWriter(self.logdir + '/test')
        acc_list = []
        for i in range(100):
            if i % 100 == 1:
                value, acc = self.sess.run([self.merged, self.accuracy], feed_dict={self.x:X, self.y:Y})
                test_writter.add_summary(value, i)
                acc_list.append(acc)
                print('Accuracy at step %s: %s' % (i, acc))
        test_writter.close()
        return acc_list


    # tensorboard execute
    def tb_exe(self, i):
        cmd = "/home/ecoc/miniconda3/envs/ecoc-demo/bin/python " \
              "/home/ecoc/miniconda3/envs/ecoc-demo/bin/tensorboard --logdir=" + self.logdir + '/train'\
              + str(i) + " --port=" + str(i)

        print(cmd)
        thread = threading.Thread(target=subprocess.getoutput, args=(cmd,))
        thread.start()
        # subprocess.getoutput(cmd)
        return str(self.get_ip_address()) + ':' + str(i)


    # reset graph
    def reset(self):
        # self.sess.close()
        return tf.reset_default_graph()


    # one-hot code
    def one_hot(self,labels,num_class):
        return tf.one_hot(labels,depth=num_class,axis=1)


    def get_ip_address(self):
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s.connect(('8.8.8.8', 80))
            ip = s.getsockname()[0]
        finally:
            s.close()
        return ip
