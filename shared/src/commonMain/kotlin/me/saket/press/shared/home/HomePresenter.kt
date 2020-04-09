package me.saket.press.shared.home

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.merge
import com.badoo.reaktive.observable.ofType
import me.saket.press.data.shared.Note
import me.saket.press.shared.editor.EditorPresenter
import me.saket.press.shared.editor.EditorPresenter.Companion.NEW_NOTE_PLACEHOLDER
import me.saket.press.shared.home.HomeEvent.NewNoteClicked
import me.saket.press.shared.home.HomeUiEffect.ComposeNewNote
import me.saket.press.shared.note.NoteRepository
import me.saket.press.shared.ui.Presenter

class HomePresenter(
  private val args: Args,
  private val repository: NoteRepository
) : Presenter<HomeEvent, HomeUiModel, HomeUiEffect> {

  override fun uiModels(publishedEvents: Observable<HomeEvent>) =
    merge(populateNotes())

  override fun uiEffects(publishedEvents: Observable<HomeEvent>) =
    publishedEvents.openNewNoteScreen()

  private fun Observable<HomeEvent>.openNewNoteScreen(): Observable<HomeUiEffect> =
    ofType<NewNoteClicked>().map { ComposeNewNote }

  private fun populateNotes(): Observable<HomeUiModel> {
    val canInclude = { note: Note ->
      args.includeEmptyNotes || (note.content.isNotBlank() && note.content != NEW_NOTE_PLACEHOLDER)
    }

    return repository.notes()
        .map { notes -> notes.filter { canInclude(it) } }
        .map {
          HomeUiModel(it.map { note ->
            val (heading, body) = SplitHeadingAndBody.split(note.content)
            HomeUiModel.Note(
                noteUuid = note.uuid,
                adapterId = note.localId,
                title = heading,
                body = body
            )
          })
        }
  }

  interface Factory {
    fun create(args: Args): HomePresenter
  }

  /**
   * Adding or removing arguments to [HomePresenter] is a lot of effort because multiple
   * DI graphs are involved. Using Dagger with AssistedInjection everywhere would have
   * been nice.
   */
  data class Args(
    /**
     * [EditorPresenter] creates a new note as soon as the editor screen is opened,
     * causing the new note to show up on home while the new note screen is opening.
     * This flag ensures that doesn't happen by ignoring empty notes when its set to
     * false. In the future, this can be set to true for multi-pane layouts on desktop
     * or tablets.
     */
    val includeEmptyNotes: Boolean
  )
}
