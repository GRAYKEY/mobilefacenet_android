/*
created by L. 2018.05.16
*/

#include "recognize.h"

namespace Face {

	Recognize::Recognize(const std::string &model_path) {
		std::string param_files = model_path + "/recognition.param";
		std::string bin_files = model_path + "/recognition.bin";
        Recognet.load_param(param_files.c_str());
        Recognet.load_model(bin_files.c_str());
	}

	Recognize::~Recognize() {
		Recognet.clear();
	}
	void Recognize::SetThreadNum(int threadNum) {
		threadnum = threadNum;
	}

	void Recognize::RecogNet(ncnn::Mat& img_) {
        feature_out.resize(128);
		ncnn::Extractor ex = Recognet.create_extractor();
		ex.set_num_threads(threadnum);
		ex.set_light_mode(true);
		ex.input("data", img_);
		ncnn::Mat out;
		ex.extract("fc1", out);
        ncnn::Mat test;
		for (int j = 0; j < 128; j++)
		{
			feature_out[j] = out[j];
		}
	}

    void Recognize::start(ncnn::Mat& ncnn_img, std::vector<float>&feature128) {
        RecogNet(ncnn_img);
        feature128 = feature_out;
    }

	double calculSimilar(std::vector<float>& v1, std::vector<float>& v2)
	{
		if(v1.size() != v2.size()||!v1.size())
			return 0;
		double ret = 0.0, mod1 = 0.0, mod2 = 0.0;
		for (std::vector<double>::size_type i = 0; i != v1.size(); ++i)
		{
			ret += v1[i] * v2[i];
			mod1 += v1[i] * v1[i];
			mod2 += v2[i] * v2[i];
		}
		return ret / sqrt(mod1) / sqrt(mod2) ;
	}
}