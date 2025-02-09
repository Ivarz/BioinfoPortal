podman stop springboot-mock && podman rm springboot-mock
sleep 3
podman run --name springboot-mock \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_PASSWORD=mypassword \
  -e POSTGRES_DB=mydb \
  -p 8765:5432 \
  -d postgres:17

sleep 3
podman cp schema.sql springboot-mock:/tmp/schema.sql
podman cp data.sql springboot-mock:/tmp/data.sql

podman exec -i springboot-mock psql -U myuser -d mydb < schema.sql
podman exec -i springboot-mock psql -U myuser -d mydb < data.sql
