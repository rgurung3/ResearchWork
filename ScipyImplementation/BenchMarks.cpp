#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <string>
#include <chrono>
#include "rectangular_lsap.h"

bool read_matrix(std::ifstream& in, std::vector<double>& cost, int& size) {
    std::string line;
    std::vector<std::vector<double>> matrix;

    while (std::getline(in, line)) {
        if (!line.empty() && line[0] == '#') break;
    }

    while (std::getline(in, line)) {
        if (line.empty()) break;

        std::stringstream ss(line);
        std::vector<double> row;
        std::string cell;
        while (std::getline(ss, cell, ',')) {
            row.push_back(std::stod(cell));
        }
        matrix.push_back(row);
    }

    size = matrix.size();
    if (size == 0 || matrix[0].size() != size) return false;

    cost.clear();
    for (const auto& row : matrix) {
        cost.insert(cost.end(), row.begin(), row.end());
    }
    return true;
}

int main() {
    std::ifstream file("../huge_benchmark_matrices.txt");
    if (!file) {
        std::cerr << "Failed to open matrix file.\n";
        return 1;
    }

    std::vector<double> cost;
    int size;
    const int repetitions = 10;
    while (read_matrix(file, cost, size)) {
        std::vector<int64_t> a(size), b(size);
        double total_time = 0.0;
        std::cout << "Starting the run";
        for (int i = 0; i < repetitions; ++i) {
            auto start = std::chrono::high_resolution_clock::now();
            int result = solve_rectangular_linear_sum_assignment(
                size, size, cost.data(), false, a.data(), b.data()
            );
            auto end = std::chrono::high_resolution_clock::now();

            if (result != 0) {
                std::cerr << "Matrix " << size << "x" << size
                          << " failed on run " << i + 1 << " with code " << result << "\n";
                break;
            }
            if(repetitions==5) {
                std::cout << "Halfway there.";
            }
            std::chrono::duration<double> elapsed = end - start;
            total_time += elapsed.count();
        }

        std::cout << "Matrix " << size << "x" << size << " | "
                  << "Average over " << repetitions << " runs: "
                  << (total_time / repetitions) << "s\n";
    }

    return 0;
}
