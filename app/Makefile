.PHONY: up down restart logs ps clean
up:
	docker compose up --build -d

down:
	docker compose down

restart:
	docker compose down
	docker compose up --build -d

logs:
	docker compose logs -f app

ps:
	docker compose ps

clean:
	docker compose down -v
