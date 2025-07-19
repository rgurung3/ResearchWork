import pandas as pd
import matplotlib.pyplot as plt

java_df = pd.read_csv("java_results.csv")
python_df = pd.read_csv("python_results.csv")

java_df['source'] = 'Java'
python_df['source'] = 'Python'

combined_df = pd.concat([java_df, python_df])
combined_df['avg_time_sec'] = combined_df['avg_time_sec'].astype(float)

avg_times = combined_df.groupby(['k', 'source'])['avg_time_sec'].mean().reset_index()

# Split k ranges
low_k = avg_times[avg_times['k'] <= 100000]
high_k = avg_times[avg_times['k'] > 100000]

# Plot 1: Full log-log scale
plt.figure(figsize=(10, 6))
for source in avg_times['source'].unique():
    subset = avg_times[avg_times['source'] == source]
    plt.plot(subset['k'], subset['avg_time_sec'], marker='o', label=source)
plt.xscale('log')
plt.yscale('log')
plt.xlabel('k (log scale)')
plt.ylabel('Average Time (sec, log scale)')
plt.title('Runtime Comparison: Java vs Python (Log-Log Scale)')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("runtime_loglog.png")
plt.close()

plt.figure(figsize=(10, 6))
for source in low_k['source'].unique():
    subset = low_k[low_k['source'] == source]
    plt.plot(subset['k'], subset['avg_time_sec'], marker='o', label=source)
plt.xlabel('k')
plt.ylabel('Average Time (sec)')
plt.title('Runtime Comparison (k ≤ 100000)')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("runtime_low_k.png")
plt.close()

plt.figure(figsize=(10, 6))
for source in high_k['source'].unique():
    subset = high_k[high_k['source'] == source]
    plt.plot(subset['k'], subset['avg_time_sec'], marker='o', label=source)
plt.xlabel('k')
plt.ylabel('Average Time (sec)')
plt.title('Runtime Comparison (k > 100000)')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("runtime_high_k.png")
plt.close()
