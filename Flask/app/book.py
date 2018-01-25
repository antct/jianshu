from flask import Blueprint, render_template, redirect,request
from app import db
from .models import User
from .models import Book
book = Blueprint('book', __name__)

@book.route('/upload',methods=['GET','POST'])
def upload():
    if request.method == 'POST':
        p_username = request.form.get('username', None)
        p_password = request.form.get('password', None)
        p_book = (request.form.get('json', None)).strip('[').strip(']').split(';')
        netBooks = Book.query.filter_by(username=p_username).all()
        if p_book == ['']:
            for i in netBooks:
                delete = Book.query.filter_by(username=i.username, isbn=i.isbn).first()
                db.session.delete(delete)
                db.session.commit()
        else:
            for i in netBooks:
                findflag = False
                for j in p_book:
                    dictj = eval(j)
                    if i.isbn == dictj['isbn']:
                        findflag = True
                        continue
                if not findflag:
                    delete = Book.query.filter_by(username=i.username, isbn=i.isbn).first()
                    db.session.delete(delete)
                    db.session.commit()
            for i in p_book:
                dicti = eval(i)
                query = Book.query.filter_by(username=p_username, isbn=dicti['isbn']).all()
                if len(query) != 0:
                    query[0].favorite = dicti['favorite']
                    query[0].content = dicti['note']
                    query[0].date = dicti['date']
                    db.session.commit()
                else:
                    insert = Book(username=p_username, isbn=dicti['isbn'], favorite=dicti['favorite'], content=dicti['note'], date=dicti['date'])
                    db.session.add(insert)
                    db.session.commit()
        return "xixi"