import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import { PdfViewerModule } from 'ng2-pdf-viewer';

@Component({
  selector: 'app-manuals',
  standalone: true,
  imports: [MatTabsModule, PdfViewerModule, MatButtonModule],
  templateUrl: './manuals.component.html',
  styleUrl: './manuals.component.css',
})
export class ManualsComponent {
  pdfSrc: string = '../../../../assets/Manuals/User.pdf';

  // Function to download the PDF file
  async downloadPDF() {
    try {
      const response = await fetch(this.pdfSrc);
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'Manual del Usuario.pdf';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Error descargando el PDF:', error);
    }
  }
}
