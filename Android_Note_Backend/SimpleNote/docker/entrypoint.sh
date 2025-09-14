#!/bin/sh
set -e

# Upgrade pip (quiet) and install deps
python -m pip install -U pip >/dev/null 2>&1 || true

if [ -f requirements.txt ]; then
  pip install --no-cache-dir -r requirements.txt
else
  # minimal deps if requirements.txt is missing
  pip install --no-cache-dir "Django>=4.2,<5.0" \
    djangorestframework djangorestframework-simplejwt \
    django-cors-headers psycopg2-binary
fi

# Migrate DB
python manage.py migrate --noinput

# (Optional) collect static for admin; harmless if unused
# python manage.py collectstatic --noinput || true

# Run dev server
exec python manage.py runserver 0.0.0.0:8000
