#pragma once
#ifndef DETECT_H_
#define DETECT_H_
#include <string>
#include <iostream>
#include "net.h"

namespace Face {

	struct Bbox
	{
		float score;
		int x1;
		int y1;
		int x2;
		int y2;
		float area;
		float ppoint[10];
		float regreCoord[4];
	};

	class Detect {
	public:
		Detect(const std::string &model_path);
		Detect(const std::vector<std::string> param_files, const std::vector<std::string> bin_files);
		~Detect();
		void SetMinFace(int minSize);
		void SetThreadNum(int threadNum);

		void start(const ncnn::Mat& img, std::vector<Bbox>& finalBbox);
	private:
		void generateBbox(ncnn::Mat score, ncnn::Mat location, std::vector<Bbox>& boundingBox_, float scale);
		void nms(std::vector<Bbox> &boundingBox_, const float overlap_threshold, std::string modelname = "Union");
		void refine(std::vector<Bbox> &vecBbox, const int &height, const int &width, bool square);
		void PNet();
		void RNet();
		void ONet();
		ncnn::Net Pnet, Rnet, Onet;
		ncnn::Mat img;
		const float nms_threshold[3] = { 0.5f, 0.7f, 0.7f };

		const float mean_vals[3] = { 127.5, 127.5, 127.5 };
		const float norm_vals[3] = { 0.0078125, 0.0078125, 0.0078125 };
		const int MIN_DET_SIZE = 12;
		std::vector<Bbox> firstBbox_, secondBbox_, thirdBbox_;
		int img_w, img_h;

	private://���ֿɵ�����
		const float threshold[3] = { 0.8f, 0.8f, 0.6f };
		int minsize = 40;
		const float pre_facetor = 0.709f;
		int threadnum = 1;
	};

	bool cmpScore(Bbox lsh, Bbox rsh);
}

#endif // !DETECT_H_
