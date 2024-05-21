package com.example.prodman.domain


data class DProduct(    val id: Int = 0,
                        val prodName: String,
                        val packaging: String,
                        val purpose: String,
                        val shelfLife: Int,
                        val netWeight: String,
                        val labeling: String,
                        val description: String,
                        val version: Int,
                        val updated: Int)