podman run -e "MINIO_API_CORS_ALLOW_ORIGIN='*'" -p 9000:9000 -p 9001:9001 minio/minio server /data --console-address ":9001"
