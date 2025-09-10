FROM postgres:16

  # Set environment variables for Postgres
ENV POSTGRES_DB=paymentsdb
ENV POSTGRES_USER=payments_user
ENV POSTGRES_PASSWORD=payments_pass

  # Copy initialization scripts
COPY init-scripts/ /docker-entrypoint-initdb.d/