package org.example.spark

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

class UniqueNames {

  val fileLocation = "/Users/tarun/tools/data/GlobalLandTemperaturesByCity.csv"

  def readFile(spark: SparkSession) = {
    implicit val dummyImplicit = org.apache.spark.sql.Encoders.javaSerialization[String]

    val df = spark.read.csv(fileLocation).map(row => row.getString(4)).groupByKey(k => k).count()

    val result = df.collect()

    result.foreach(row => {
      println(row._1 + ":" + row._2)
    })
  }

  def readFile2(spark: SparkSession) = {
    implicit val dummyImplicit = org.apache.spark.sql.Encoders.javaSerialization[String]

    val df = spark.read.csv(fileLocation).rdd.map(row => (row.getString(4), 1)).reduceByKey(_ + _)
    val result = df.collect()

    result.foreach(row => {
      println(row._1 + ":" + row._2)
    })
  }
}

object UniqueNames {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()

    val sparkSession = SparkSession.builder()
      .master("spark://192.168.1.12:7077").config(sparkConf).getOrCreate()

    val runner = new UniqueNames

    runner.readFile(sparkSession)
  }
}