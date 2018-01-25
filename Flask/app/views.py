from app import app
from .user import user
from .book import book

app.register_blueprint(user, url_prefix='/user')
app.register_blueprint(book, url_prefix='/book')