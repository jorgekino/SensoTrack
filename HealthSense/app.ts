import express, { Request, Response } from 'express';
import { sequelize } from './src/models/sequelize';
import routes from './src/routes/index';
import './src/models/index';

;(async () => {
  try {
    await sequelize.authenticate();
    console.log('Database connection established successfully.');

    // Sincronizar modelos
    await sequelize.sync({ force: false });
    console.log('Models synchronized successfully.');

    const app = express()
    const port = 3000

    app.use(express.json());

    app.use('/',routes)

    app.listen(port, () => {
      console.log(`Health Sense app listening on port ${port}`)
    })

  } catch (error) {
    console.error('Unable to connect to the database:', error);
  }
})()