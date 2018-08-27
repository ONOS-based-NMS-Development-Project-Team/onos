import tensorflow as tf
import numpy as np
x_data = np.random.rand(100)
y_data = 0.2 * x_data + 1
print(len(y_data))

a = tf.Variable(0.6)
b = tf.Variable(0.4)
# c = tf.Variable(tf.zeros([1], dtype=tf.float64))
c = tf.Variable(tf.random_normal([1], dtype=tf.float32, seed=1))
y = c * x_data + b

loss = tf.reduce_mean(tf.square(y_data - y))
optimizer = tf.train.GradientDescentOptimizer(0.2)
train = optimizer.minimize(loss)
init = tf.global_variables_initializer()
sess = tf.Session()
sess.run(init)
for step in range(201):
    if step%20 == 0:
        sess.run(train)
        print(step,sess.run([c,b]))
        print(sess.run(loss,feed_dict={x_data:100}))