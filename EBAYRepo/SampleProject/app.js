var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var http = require("http");
var User = require('./models/user');
var fs = require('fs');
var https = require('https');


var routes = require('./routes/index');
var users = require('./routes/users');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', routes);
app.use('/users', users);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});


module.exports = app;

/** Create data for DB *************/
//create a new user called chris
var chris = new User({
  name: 'Maissie',
  username: 'mais',
  password: 'password',
  admin: true 
});

// call the custom method. this will just add -dude to his name
// user will now be Chris-dude
chris.dudify(function(err, name) {
  if (err) throw err;

  console.log('Your new name is ' + name);
});

// call the built-in save method to save to the database
chris.save(function(err) {
  if (err) throw err;

  console.log('User saved successfully!');
});

//get all the users
User.find({}, function(err, users) {
  if (err) throw err;

  // object of all the users
  console.log(users);
});

//get the user jasferna
User.find({ username: 'jasferna' }, function(err, user) {
  if (err) throw err;

  // object of the user
  console.log(user);
});
/*******************************************/




/*http.createServer(function (request, response) {
}).listen(3000);*/

https.createServer({
    key: fs.readFileSync('C:/openssl-0.9.8k_X64/bin/key.pem'),
    cert: fs.readFileSync('C:/openssl-0.9.8k_X64/bin/cert.pem'),
    passphrase: 'root'
  }, app).listen(3000, function() {
		console.log('Express server listening on port: 3000');
});

// Console will print the message
//console.log('Server running at http://localhost:3000/');