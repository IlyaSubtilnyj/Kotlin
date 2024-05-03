package com.example.application.models

import android.content.Intent
import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.application.databinding.TmplSignInBinding
import java.io.Serializable
import java.util.ArrayList

class Segue {

    private lateinit var _context: CapableActivity<*>
    private lateinit var _destination: Class<*>

    private lateinit var _context2: Context

    public fun forward(data: List<Pair<String, Serializable?>>) {

        this.prepareIntent(data)
    }

    public fun redirect(data: List<Pair<String, Serializable?>>) {

        this.prepareIntent(data)
        _context.finish()
    }

    constructor(_context: CapableActivity<*>, _destination: Class<*>)
    {
        this._context = _context
        this._destination = _destination
    }

    constructor(_context: Context, _destination: Class<*>){

        this._context2 = _context
        this._destination = _destination
    }

    private fun prepareIntent(destinationTransfer: List<Pair<String, Serializable?>>) {

        val intent = Intent(_context, _destination)
        for (transfer in destinationTransfer) {

            val value = transfer.second
            when (value) {
                is List<*> -> {
                    val stringList = value as? List<String>
                    intent.putStringArrayListExtra(transfer.first, ArrayList(stringList))
                }
                else -> intent.putExtra(transfer.first, value)
            }

        }
        _context.startActivity(intent)
    }

    operator fun invoke(destinationTransfer: List<Pair<String, Serializable?>> = emptyList()) {

        val intent = Intent(_context2, _destination)
        for (transfer in destinationTransfer) {

            val value = transfer.second
            when (value) {
                is List<*> -> {
                    val stringList = value as? List<String>
                    intent.putStringArrayListExtra(transfer.first, ArrayList(stringList))
                }
                else -> intent.putExtra(transfer.first, value)
            }

        }
        _context2.startActivity(intent)
    }
}
