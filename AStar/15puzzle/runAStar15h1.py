import subprocess

def run_a_star(iterations):
    for i in range(iterations):
        print(f'Running iteration {i + 1}...')
        result = subprocess.run(['java', 'AStar15h1'], capture_output=True, text=True, encoding='utf-8')
        output = result.stdout
        print("Java program output:", output)  # Javaプログラムの出力を表示して確認

run_a_star(10)
