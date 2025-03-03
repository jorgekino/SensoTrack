import { Router } from "express";
import { processSensorData } from "../pipes-and-filters";

const routes = Router();

routes.get('/', (req, res) => {
  res.send('Health Sense!')
})

routes.post('/sensorData', (req, res) => {
  try {
    processSensorData(req.body)
    
    res.status(201).send('Sensor data processed successfully');
  } catch (error) {
    res.status(500).send('Error processing sensor data');
  }
})

export default routes;
