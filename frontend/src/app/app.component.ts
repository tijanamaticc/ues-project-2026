import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProjectuesApiService } from './services/projectues-api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  readonly app = inject(ProjectuesApiService);

  ngOnInit(): void {
    this.app.init();
  }

  scrollToPreview(): void {
    document.getElementById('preview')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
}
