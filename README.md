# mobilefacenet_android
A simple android demo for mobilefacenet on ncnn

---
# 简介
人脸检测算法使用mtcnn，人脸识别算法使用mobilefacenet。<br>
前向卷积框架使用ncnn。<br>
简单测试demo，没有人脸对齐部分。<br>

---
# 注意事项
第一次运行可能出现未检测到人脸的情况。确保根目录下存在模型文件夹。清理内存后再重启运行，多试几次。这个问题我不知道怎么解决。

---
# 效果
同个人<br>
![image](https://github.com/GRAYKEY/mobilefacenet_android/blob/master/result/same.png)<br>
不同人<br>
![image](https://github.com/GRAYKEY/mobilefacenet_android/blob/master/result/different.png)<br>
