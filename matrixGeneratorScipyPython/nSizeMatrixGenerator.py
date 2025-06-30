import random
import os

def write_matrix_to_txt(file, size):
    file.write(f"# Matrix size: {size}x{size}\n")
    for _ in range(size):
        row = [str(random.randint(1, 1000)) for _ in range(size)]
        file.write(",".join(row) + "\n")
    file.write("\n") 

def generate_large_matrices(sizes, output_path="huge_benchmark_matrices.txt"):
    with open(output_path, "w") as file:
        for size in sizes:
            print(f"Writing {size}x{size} matrix...")
            write_matrix_to_txt(file, size)
            print(f"Done: {size}x{size}\n")

if __name__ == "__main__":
    sizes = [10000, 20000, 30000, 40000, 50000]
    generate_large_matrices(sizes)
