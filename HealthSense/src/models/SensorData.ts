import { DataTypes } from 'sequelize';
import { sequelize } from './sequelize';

export const SensorData = sequelize.define('SensorData', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true,
  },
  sensorId: {
    type: DataTypes.INTEGER,
    allowNull: false,
  },
  sensorType: {
    type: DataTypes.STRING,
    allowNull: false,
  },
  timestamp: {
    type: DataTypes.DATE,
    allowNull: false, 
  },
  lightValue: {
    type: DataTypes.INTEGER,
    allowNull: true,
  },
  latitude: {
    type: DataTypes.DOUBLE,
    allowNull: true,
  },
  longitude: {
    type: DataTypes.DOUBLE,
    allowNull: true,
  },
  action: {
    type: DataTypes.STRING,
    allowNull: true,
  },
}, {
  tableName: 'SensorData',
});
