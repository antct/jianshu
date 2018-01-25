from flask import Blueprint, render_template, redirect,request
from app import db
from .models import User
from .models import Book
user = Blueprint('user',__name__)

@user.route('/login',methods=['GET','POST'])
def login():
    if request.method == 'POST':
        p_username = request.form.get('username', None)
        p_password = request.form.get('password', None)
        print("username:", p_username, "password:", p_password)
        query = User.query.filter_by(username=p_username).all()
        if len(query) == 0:
            return "error"
        else:
            if query[0].password != p_password:
                return "error1"
            else:
                book_query = Book.query.filter_by(username=p_username).all()
                s = ''
                for i in range(0, len(book_query)):
                    item = book_query[i]
                    t = {}
                    t["isbn"] = item.isbn
                    t["favorite"] = item.favorite
                    t["note"] = item.content
                    t["date"] = item.date
                    s += str(t)
                    if i != len(book_query) - 1:
                        s += ';'
                return s




@user.route('/signup',methods=['GET','POST'])
def signup():
    if request.method == 'POST':
        p_username = request.form.get('username', None)
        p_password = request.form.get('password', None)
        print(p_username, p_password)
        query = User.query.filter_by(username=p_username).all()
        if len(query) != 0:
            return "error2"
        else:
            insert = User(username=p_username, password=p_password)
            db.session.add(insert)
            db.session.commit()
            return "signup"
