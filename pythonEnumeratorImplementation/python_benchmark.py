import numpy as np
from enumerator import enumerator
from timer import Timer
import time
import csv

def load_matrix(seed=0, n=10):
    return np.loadtxt(f"../cost_matrix_{seed}.txt", dtype=int)

def run_benchmark(ks, runs_per_k=10, n=10):
    matrix_id = 0
    matrix = load_matrix(matrix_id, n)

    with open("../python_results.csv", mode="w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(["matrix_id", "k", "avg_time_sec"])

        for k in ks:
            total_time = 0.0

            for run in range(runs_per_k):
                start = time.time()
                with Timer(f"matrix {matrix_id}, k = {k}"):
                    enumerator(matrix, k)
                end = time.time()
                total_time += (end - start)

            avg_time = total_time / runs_per_k
            writer.writerow([matrix_id, k, f"{avg_time:.6f}"])
            print(f"{matrix_id},{k},{avg_time:.6f} sec")

if __name__ == "__main__":
    ks = [1000, 5000, 10000, 25000, 50000, 100000, 500000, 1000000, 2000000, 3000000, 3700000]
    run_benchmark(ks)
