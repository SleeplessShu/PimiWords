package com.sleeplessdog.matchthewords.game.presentation.ingameFragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameTrueOrFalseBinding
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import com.sleeplessdog.matchthewords.game.presentation.models.TOFButtonState
import com.sleeplessdog.matchthewords.game.presentation.models.TfQuestionUi
import com.sleeplessdog.matchthewords.game.presentation.parentControllers.SwipeTouchListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class TrueOrFalseFragment : Fragment(R.layout.game_true_or_false) {
    private val parentVM: GameViewModel by sharedViewModel(
        owner = { requireParentFragment() })
    private val childVM: TrueOrFalseViewModel by viewModel()

    private var _binding: GameTrueOrFalseBinding? = null
    private val binding: GameTrueOrFalseBinding get() = _binding!!

    private lateinit var topCard: View
    private lateinit var nextCard: View
    private fun wordView(v: View) = v.findViewById<TextView>(R.id.t_word)
    private fun translateView(v: View) = v.findViewById<TextView>(R.id.t_translate)

    private var isLocked: Boolean = false
    private var currentIsCorrect: Boolean = false

    private data class TofButton(
        val root: View, val icon: ImageView, val isTrue: Boolean
    )

    private lateinit var btnTrue: TofButton
    private lateinit var btnFalse: TofButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = GameTrueOrFalseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topCard = binding.wordCardA.root
        nextCard = binding.wordCardB.root
        nextCard.visibility = View.INVISIBLE

        // кнопки
        btnTrue = TofButton(binding.btnTrue, binding.icTrue, isTrue = true)
        btnFalse = TofButton(binding.btnFalse, binding.icFalse, isTrue = false)

        // клики
        binding.btnTrue.setOnClickListener { onButtonPressed(btnTrue, true) }
        binding.btnFalse.setOnClickListener { onButtonPressed(btnFalse, false) }

        // визуальный "press" при удержании (не мешаем клику)
        setPressTouch(binding.btnTrue, btnTrue)
        setPressTouch(binding.btnFalse, btnFalse)

        attachSwipeTo(topCard)
        setupObservers()
    }

    private fun setPressTouch(v: View, b: TofButton) {
        v.setOnTouchListener { _, e ->
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> b.applyState(TOFButtonState.PRESSED)
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    if (!isLocked) b.applyState(TOFButtonState.DEFAULT)
                }
            }
            false // не гасим click
        }
    }

    private fun onButtonPressed(button: TofButton, isRightBtn: Boolean) {
        if (isLocked) return
        // имитация нажатия
        button.applyState(TOFButtonState.PRESSED)
        commitAnswer(isRightBtn, pressedButton = button)
    }


    private fun setupObservers() {
        parentVM.wordsPairs.observe(viewLifecycleOwner) { pool ->
            if (pool.isNullOrEmpty()) return@observe
            childVM.setPool(pool)
            binding.root.isVisible = true
        }

        childVM.ui.observe(viewLifecycleOwner) { ui ->
            if (ui == null) return@observe
            currentIsCorrect = ui.isCorrect

            // заполняем АКТИВНУЮ карточку (topCard)
            bindCard(topCard, ui)
            topCard.background = requireContext().getDrawable(R.drawable.bg_tof_card_r24)
            btnTrue.applyState(TOFButtonState.DEFAULT)
            btnFalse.applyState(TOFButtonState.DEFAULT)
            isLocked = ui.locked
        }


        childVM.events.observe(viewLifecycleOwner) { e ->
            parentVM.onGameEvent(e)
        }
    }

    private fun bindCard(card: View, model: TfQuestionUi) {
        wordView(card).text = model.word.text
        translateView(card).text = model.shownTranslation.text
        // дефолтный фон
        card.background = requireContext().getDrawable(R.drawable.bg_tof_card_r24)
    }

    private fun attachSwipeTo(card: View) {
        card.setOnTouchListener(
            SwipeTouchListener(
            card = card,
            wouldBeCorrect = { isRight -> isRight == currentIsCorrect },
            onSwipeRightCommit = { commitAnswer(true, pressedButton = btnTrue) },
            onSwipeLeftCommit = { commitAnswer(false, pressedButton = btnFalse) },
            canSwipe = { childVM.ui.value?.locked == false }))
    }


    private fun commitAnswer(
        isRight: Boolean, pressedButton: TofButton = if (isRight) btnTrue else btnFalse
    ) {
        if (isLocked) return
        isLocked = true

        // подготовка nextCard
        val preview = childVM.peekNext()
        if (preview != null) {
            bindCard(nextCard, preview)
            nextCard.apply {
                visibility = View.VISIBLE
                alpha = 0f; translationY = 24f; scaleX = 0.98f; scaleY = 0.98f
            }
        }

        val ok = (isRight == currentIsCorrect)
        val color = if (ok) R.color.green_primary else R.color.red_primary

        (topCard.background?.mutate())?.let { d ->
            DrawableCompat.setTint(d, colorWithAlpha(requireContext().getColor(color), 0.6f))
            topCard.background = d
        }

        pressedButton.applyState(if (ok) TOFButtonState.RESULT_CORRECT else TOFButtonState.RESULT_WRONG)

        val other = if (pressedButton.isTrue) btnFalse else btnTrue
        other.applyState(TOFButtonState.DEFAULT)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(PAUSE_BEFORE_REACTION)

            animateParallelSwap(isRight) {
                if (isRight) childVM.onTrueClicked() else childVM.onFalseClicked()
                childVM.advanceNow()

                btnTrue.applyState(TOFButtonState.DEFAULT)
                btnFalse.applyState(TOFButtonState.DEFAULT)

                swapCards()
                attachSwipeTo(topCard)
                isLocked = false
            }
        }
    }

    private fun animateParallelSwap(isRight: Boolean, end: () -> Unit) {
        val dir = if (isRight) 1 else -1

        // top уезжает
        topCard.animate().translationX(dir * topCard.width.toFloat())
            .translationY(-topCard.height * 0.35f).rotation(dir * 35f).alpha(0f).setDuration(220)
            .start()

        // next въезжает
        nextCard.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f).setDuration(220)
            .withEndAction(end).start()
    }

    private fun swapCards() {
        // вернуть старую topCard в исходное невидимое состояние — станет «next»
        topCard.apply {
            translationX = 0f
            translationY = 0f
            rotation = 0f
            alpha = 1f
            visibility = View.INVISIBLE
            background = requireContext().getDrawable(R.drawable.bg_tof_card_r24)
            setOnTouchListener(null)
        }
        // поменять ссылки
        val tmp = topCard
        topCard = nextCard
        nextCard = tmp
    }

    // утилита для альфы
    private fun colorWithAlpha(color: Int, alpha: Float): Int {
        val a = (alpha.coerceIn(0f, 1f) * 255).toInt()
        val r = Color.red(color);
        val g = Color.green(color);
        val b = Color.blue(color)
        return Color.argb(a, r, g, b)
    }

    private fun TofButton.applyState(state: TOFButtonState) {
        root.setBackgroundResource(state.backgroundRes)

        // базовый тинт: зелёный для true, красный для false — в DEFAULT/PRESSED
        val autoTint = if (isTrue) R.color.green_primary else R.color.red_primary
        val tint = when (state) {
            TOFButtonState.DEFAULT, TOFButtonState.PRESSED -> autoTint
            else -> state.tintColorRes
        }
        icon.imageTintList = ColorStateList.valueOf(requireContext().getColor(tint))
        root.translationY = state.offsetY
    }

    private companion object {
        const val PAUSE_BEFORE_REACTION = 200L
    }
}

