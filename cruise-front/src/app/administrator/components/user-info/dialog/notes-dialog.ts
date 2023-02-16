import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { UserNotesDTO } from '../../../models/user-notes-dto';
import { Subscription } from 'rxjs';
import { UserInfoService } from '../../../services/user-info.service';

@Component({
  selector: 'app-notes-dialog',
  templateUrl: './notes-dialog.html',
  styleUrls: ['./notes-dialog.css'],
})

// eslint-disable-next-line @angular-eslint/component-class-suffix
export class NotesDialog implements OnInit, OnDestroy {
  notes!: UserNotesDTO;
  notesSupscription: Subscription = new Subscription();
  idSupscription: Subscription = new Subscription();
  constructor(
    private dialog: MatDialog,
    private userInfoService: UserInfoService
  ) {}

  ngOnInit(): void {
    this.idSupscription = this.userInfoService.selectedId$.subscribe((id) => {
      this.notesSupscription = this.userInfoService
        .getUserNotes(id)
        .subscribe((notes) => {
          this.notes = notes;
        });
    });
  }

  ngOnDestroy(): void {
    this.notesSupscription.unsubscribe();
    this.idSupscription.unsubscribe();
  }
}
