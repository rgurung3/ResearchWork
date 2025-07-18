import matplotlib.pyplot as plt

ks = [1000, 5000, 10000, 25000, 50000, 100000, 500000, 1000000, 2000000, 3000000, 3700000]
times = [52, 198, 365, 835, 1774, 3625, 19827, 40894, 87401, 136723, 169677]

plt.figure(figsize=(10, 6))
plt.plot(ks, times, marker='o', linestyle='-', color='purple')
plt.xscale('log')
plt.yscale('log')
plt.title("Murty Runtime", fontsize=16)
plt.xlabel("k (log)", fontsize=14)
plt.ylabel("Time (ms, log)", fontsize=14)
plt.grid(axis= 'y')
plt.savefig("murty_runtime_loglog.png")
plt.close()

plt.figure(figsize=(10, 6))
plt.plot(ks[:6], times[:6], marker='o', linestyle='-', color='green')
plt.title("Murty Runtime (k = 1000 to 100000)", fontsize=16)
plt.xlabel("k", fontsize=14)
plt.ylabel("Time (ms)", fontsize=14)
plt.grid(True)
plt.savefig("murty_runtime_small.png")
plt.close()

plt.figure(figsize=(10, 6))
plt.plot(ks[6:], times[6:], marker='o', linestyle='-', color='red')
plt.title("Murty Runtime (k = 500000 to 3700000)", fontsize=16)
plt.xlabel("k", fontsize=14)
plt.ylabel("Time (ms)", fontsize=14)
plt.grid(True)
plt.savefig("murty_runtime_large.png")
plt.close()