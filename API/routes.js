var fs = require('fs');
var rn = require('random-number');

module.exports = function(app, db) {
  app.get('/api/:product/all', (req, res) => {
    let name, price, image;
    switch (req.params.product) {
      case 'prod1':
        name = 'Product 1'
        price = 80
        image = fs.readFileSync('./img/1.png', 'base64');
        break;
      case 'prod2':
        name = 'Product 2'
        price = 100
        image = fs.readFileSync('./img/2.png', 'base64');
        break;
      case 'prod3':
        name = 'Product 3'
        price = 90
        image = fs.readFileSync('./img/3.png', 'base64');
        break;
      case 'prod4':
        name = 'Product 4'
        price = 80
        image = fs.readFileSync('./img/4.png', 'base64');
        break;
      case 'prod5':
        name = 'Product 5'
        price = 85
        image = fs.readFileSync('./img/5.png', 'base64');
        break;
      case 'prod6':
        name = 'Product 6'
        price = 90
        image = fs.readFileSync('./img/6.png', 'base64');
        break;
      case 'prod7':
        name = 'Product 7'
        price = 80
        image = fs.readFileSync('./img/7.png', 'base64');
        break;
      case 'prod8':
        name = 'Product 8'
        price = 70
        image = fs.readFileSync('./img/8.png', 'base64');
        break;
      default:
        res.status(422);
    }

    res.send({
      name,
      price,
      image
    });
  });

  app.get('/api/:product/price', (req, res) => {
    if (['prod1', 'prod2', 'prod3', 'prod4', 'prod5', 'prod6', 'prod7', 'prod8'].indexOf(req.params.product) == -1) {
      res.status(422);
    } else {
      let prices = [70, 75, 80, 85, 90, 95, 100, 105, 110];

      var gen = rn.generator({
        min: 0,
        max: 8,
        integer: true
      });

      let price = prices[gen()];

      res.send({
        price
      });
    }
  });
};