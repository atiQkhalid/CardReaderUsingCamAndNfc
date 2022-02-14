package com.example.Native.views.activities.nfc

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.example.Native.R
import com.example.Native.base.BaseActivity
import com.example.Native.cardreader.CardNfcAsyncTask
import com.example.Native.cardreader.CardNfcAsyncTask.CardNfcInterface
import com.example.Native.cardreader.utils.CardNfcUtils
import com.example.Native.databinding.ActivityNfcCardReaderBinding
import com.example.Native.extenssions.replaceFragmentSafely
import com.example.Native.utils.Constants
import com.example.Native.views.fragments.result.ScannerResultFragment
import pl.droidsonroids.gif.GifImageView


class NFCscanActivity : BaseActivity(), CardNfcInterface {

    private lateinit var binding: ActivityNfcCardReaderBinding

    private var mCardNfcAsyncTask: CardNfcAsyncTask? = null
    private var mNfcAdapter: NfcAdapter? = null
    private var mTurnNfcDialog: AlertDialog? = null
    private var mProgressDialog: ProgressDialog? = null
    private var mDoNotMoveCardMessage: String? = null
    private var mUnknownEmvCardMessage: String? = null
    private var mCardWithLockedNfcMessage: String? = null
    private var cardNumber: String? = null
    private var cardType: String? = null
    private var cardCvc: String? = null
    private var expiredDate: String? = null
    private var mIsScanNow = false
    private var mIntentFromCreate = false
    private var mCardNfcUtils: CardNfcUtils? = null
    private var imgRead: ImageView? = null
    private var llText: LinearLayout? = null
    private var imgRight: GifImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNfcCardReaderBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        imgRight = findViewById(R.id.imgRight)
        imgRead = findViewById(R.id.imgRead)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        llText = findViewById(R.id.llText)
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC not connected", Toast.LENGTH_LONG).show()
            // atiQ
        } else {
            mCardNfcUtils = CardNfcUtils(this)
            createProgressDialog()
            initNfcMessages()
            mIntentFromCreate = true
            onNewIntent(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        mIntentFromCreate = false
        if (mNfcAdapter != null && !mNfcAdapter!!.isEnabled) {
            showTurnOnNfcDialog()
        } else if (mNfcAdapter != null) {
            if (!mIsScanNow) {
                Toast.makeText(this, "Not scanned yet", Toast.LENGTH_LONG).show()
            }
            mCardNfcUtils!!.enableDispatch()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) {
            mCardNfcUtils!!.disableDispatch()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (mNfcAdapter != null && mNfcAdapter!!.isEnabled) {
            mCardNfcAsyncTask = CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate)
                .build()
        }
    }

    override fun startNfcReadCard() {
        mIsScanNow = true
        mProgressDialog!!.show()
    }

    override fun cardIsReadyToRead() {
        cardNumber = mCardNfcAsyncTask!!.cardNumber.take(16)
        expiredDate = mCardNfcAsyncTask!!.cardExpireDate.take(5)
        cardType = mCardNfcAsyncTask!!.cardType
        cardCvc = mCardNfcAsyncTask!!.cardCvv.toString()
        imgRight!!.visibility = View.VISIBLE
        llText!!.visibility = View.GONE
        Toast.makeText(this, "$cardType card detected", Toast.LENGTH_LONG).show()

            binding.layoutCardScanner.visibility = View.GONE

            val cardDetails = Bundle().also {
                it.putString(Constants.CARD_NUMBER, cardNumber)
                it.putString(Constants.EXPIRY, expiredDate)
                it.putString(Constants.CVC, cardCvc)
            }
            replaceFragmentSafely(ScannerResultFragment(), bundle = cardDetails)


        parseCardType(cardType)
    }

    override fun doNotMoveCardSoFast() {
        showSnackBar(mDoNotMoveCardMessage)
    }

    override fun unknownEmvCard() {
        showSnackBar(mUnknownEmvCardMessage)
    }

    override fun cardWithLockedNfc() {
        showSnackBar(mCardWithLockedNfcMessage)
    }

    override fun finishNfcReadCard() {
        mProgressDialog!!.dismiss()
        mCardNfcAsyncTask = null
        mIsScanNow = false
    }

    private fun createProgressDialog() {
        val title = "Scanning ..."
        val mess = "Please do not remove or move card during reading."
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setTitle(title)
        mProgressDialog!!.setMessage(mess)
        mProgressDialog!!.isIndeterminate = true
        mProgressDialog!!.setCancelable(false)
    }

    private fun showSnackBar(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showTurnOnNfcDialog() {
        if (mTurnNfcDialog == null) {
            val title = "NFC is turned off."
            val mess = "You need turn on NFC module for scanning. Wish turn on it now?"
            val pos = "Turn on"
            val neg = "Dismiss"
            mTurnNfcDialog = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(mess)
                .setPositiveButton(
                    pos
                ) { dialogInterface, i -> // Send the user to the settings page and hope they turn it on
                    startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                }
                .setNegativeButton(
                    neg
                ) { dialogInterface, i -> onBackPressed() }.create()
        }
        mTurnNfcDialog!!.show()
    }

    private fun initNfcMessages() {
        mDoNotMoveCardMessage = "don't remove card"
        mCardWithLockedNfcMessage = "NFC is locked on this card."
        mUnknownEmvCardMessage = "unknown ENV card"
    }

    private fun parseCardType(cardType: String?) {
        when (cardType) {
            CardNfcAsyncTask.CARD_UNKNOWN -> {
                Toast.makeText(this, "This is unknown bank card.", Toast.LENGTH_LONG)
                    .show()
            }
            CardNfcAsyncTask.CARD_VISA -> {
                Toast.makeText(this, "Visa card detected", Toast.LENGTH_LONG).show()
            }
            CardNfcAsyncTask.CARD_MASTER_CARD -> {
                Toast.makeText(this, "Master card detected", Toast.LENGTH_LONG).show()
            }
        }
    }
}