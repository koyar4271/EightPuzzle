import subprocess
import csv

def run_a_star(iterations):
    with open('results_h0_3.csv', 'w', newline='') as csvfile:
            #!check the name of file
        fieldnames = ['Iteration', 'Search Cost']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()

        for i in range(iterations):
            print(f'Running iteration {i + 1}...')
            result = subprocess.run(['java', '-cp', 'AStarh0.jar', 'AStarh0'], capture_output=True, text=True, encoding='utf-8')
                                #!select .jar file whtch to run
            output = result.stdout  # 標準出力をキャプチャする
            lines = output.strip().splitlines()  # 改行文字を削除してから行を分割する
            search_cost = lines[-1]  # 最後の行を取得する
            writer.writerow({'Iteration': i + 1, 'Search Cost': search_cost})

run_a_star(10)
