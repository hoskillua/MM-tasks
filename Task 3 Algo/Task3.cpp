#include <iostream>
#include <vector>
#include <algorithm>
#include <functional>
#define CRT_SECURE_NO_WARNINGS

template <typename DataType>
struct Flattened3DMat {
	int n, m, p;
	std::vector<DataType> data;

	/// Req 1: Creating vector from given dimensions
	Flattened3DMat(int n, int m, int p) {
		data = std::vector<DataType>(n * m * p);
		this->n = std::max(n, 0);
		this->m = std::max(m, 0);
		this->p = std::max(p, 0);
	}

	/// Req 1: Creating vector from Matrix
	// For simplicity: Assumes no inner vectors have different dimensions:
	// for example: [ [ [1] , [2,3] ], [3,4] ] is not valid 
	Flattened3DMat(std::vector <std::vector <std::vector <DataType>>> Mat) {
		n = Mat.size();
		if (n)
			m = Mat[0].size();
		if (m)
			p = Mat[0][0].size();
		data = std::vector<DataType>(n * m * p);
		readMatrix(Mat);
	}

	/// Req 2: Indexing
	inline DataType &at(int i, int j, int k) {
		return data.at(i * m * p + j * p + k);
	}

	/// Helper Functions
	void readMatrix(std::vector <std::vector <std::vector <DataType>>> Mat){
		for(int i = 0; i < std::min((int)Mat.size(), n); i++)
			for (int j = 0; j < std::min((int)Mat[i].size(), m); j++)
				for (int k = 0; k < std::min((int)Mat[i][j].size(), p); k++)
					at(i, j, k) = Mat[i][j][k];

	}

	std::vector <std::vector <std::vector <DataType>>> 
		getMatrix() {
		std::vector <std::vector <std::vector <DataType>>> Mat
		(n, (std::vector <std::vector <DataType>>(m, std::vector<DataType>(p))));
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				for (int k = 0; k < p; k++)
					Mat[i][j][k] = at(i, j, k);
		return Mat;
	}

	void fillByFunction(std::function<DataType(int, int, int)> function) {
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				for (int k = 0; k < p; k++)
					at(i, j, k) = function(i, j, k);
	}

	void print() {
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++) {
				for (int k = 0; k < p; k++)
					std::cout << "vec[" << i << ", " << j << ", " << k << "]: " << at(i, j, k) << " , ";
				std::cout << std::endl;
			}
		std::cout << std::endl;
	}
};

int main() {

	/// Testing Creating vector with dimensions
	int n = 2, m = 3, p = 4;
	Flattened3DMat<int> vec1(n, m, p);
	std::cout << "Test1: Testing Creating vector with dimensions:" << std::endl << std::endl;
	vec1.print();

	/// Testing Indexing
	for(int i = 0; i < n; i++)
		for (int j = 0; j < m; j++)
			for (int k = 0; k < p; k++)
				vec1.at(i, j, k) = (i + j) * k;
	std::cout << "Test2: Testing Indexing:" << std::endl << std::endl;
	vec1.print();

	/// Testing Matrix Coonversions
	// vec2 is identical to vec1 as it is constructed by the matrix of vec1
	Flattened3DMat<int> vec2(vec1.getMatrix());
	std::cout << "Test3: Testing Matrix Coonversions:" << std::endl << std::endl;
	vec2.print();

	/// Testing Filling by provided function (parity of sum) 
	vec2.fillByFunction([](int i, int j, int k) -> int {
		return (i + j + k) % 2;
	});
	std::cout << "Test4: Testing Filling by provided function (parity of sum):" << std::endl << std::endl;
	vec2.print();
}
