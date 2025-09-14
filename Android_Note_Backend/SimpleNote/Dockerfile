FROM python:3.12-bullseye
WORKDIR /app
COPY requirements.txt .

# Install the dependencies.
RUN pip install --no-cache-dir -r requirements.txt

# Copy the current directory contents into the container at /app.
COPY . .

# Set the environment variables for Django.
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# Expose port 8000 for the Django app to run on.
EXPOSE 8000

# Set the DJANGO_SETTINGS_MODULE variable for production
ENV DJANGO_SETTINGS_MODULE=SimpleNote.settings.production

# Run the Django development server.
CMD ["sh", "-c", "python manage.py migrate && python manage.py runserver 0.0.0.0:8000"]






# SimpleNote / Dockerfile
FROM python:3.10-slim

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

WORKDIR /app

# (Optional) system deps if you later add Pillow etc.
RUN apt-get update && apt-get install -y --no-install-recommends build-essential && \
    rm -rf /var/lib/apt/lists/*

# Copy project
COPY . /app

# Install python deps (minimal set matching your project)
RUN pip install --no-cache-dir "Django>=4.2,<5.0" djangorestframework djangorestframework-simplejwt django-cors-headers

# Entrypoint runs migrate + dev server
COPY docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8000
ENTRYPOINT ["/entrypoint.sh"]
