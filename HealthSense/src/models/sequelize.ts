import { Sequelize } from 'sequelize'
import * as dotenv from 'dotenv';

dotenv.config();

export const sequelize = new Sequelize(
  process.env.DB_NAME!,
  process.env.DB_USER!,
  process.env.DB_PASS,
  {
    host: process.env.DB_HOST,
    dialect: process.env.DB_DIALECT as any,
    port: process.env.DB_PORT as any,
    dialectOptions: {
      connectTimeout: 60000,
    },
    define: {
      timestamps: false,
    },
    logging: process.env.VERBOSE === 'true' ? console.log : false,
  },
)
