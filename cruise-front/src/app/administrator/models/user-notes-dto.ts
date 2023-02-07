import {NoteDTO} from "../../user/models/note-dto";
import {UserNoteDTO} from "./user-note-dto";

export interface UserNotesDTO {
  totalCount: number,
  results: UserNoteDTO[]
}
