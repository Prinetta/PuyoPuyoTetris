package com.puyo

class Grid (val width: Int, val length: Int){
    val array = Array(width) {Array<Block?>(length) {null} }
}
