cd keycloak && bash run_kc.sh &
cd minio && bash launch_minio.sh &
cd rabbitmq && bash launch_rabbit.sh &
cd mock_sequence_db && bash run_mock.sh &
