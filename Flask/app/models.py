from app import db

class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(32), unique=True)
    password = db.Column(db.String(32), nullable=False)

class Book(db.Model):
    __tablename__ = 'books'
    book_id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(32))
    isbn = db.Column(db.String(32), nullable=False)
    favorite = db.Column(db.String(10))
    content = db.Column(db.String(200))
    date = db.Column(db.String(20))