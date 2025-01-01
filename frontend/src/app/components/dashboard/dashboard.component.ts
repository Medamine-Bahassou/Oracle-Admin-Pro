import {Component, OnInit, OnDestroy} from '@angular/core';
import { Chart, ChartConfiguration, ChartType } from 'chart.js/auto';
import {PerformanceService} from "../../services/performance/performance.service";
import { Subject, takeUntil } from 'rxjs';
import {AwrService} from "../../services/performance/awr.service";
import {AshService} from "../../services/performance/ash.service";
import jsPDF from 'jspdf';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, OnDestroy {
  cpuChart: any;
  memoryChart: any;
  ioChart: any;
  awrReport: any[] = [];
  ashReport: any[] = [];
  private destroy$ = new Subject<void>();

  constructor(private performanceService: PerformanceService, private awrService: AwrService, private ashService: AshService) {}

  ngOnInit() {
    this.createCharts();
    this.fetchMetrics();
    this.fetchAwrReport();
    this.fetchAshReport();
  }


  fetchMetrics() {
    this.performanceService.getCurrentMetrics().pipe(takeUntil(this.destroy$)).subscribe({
      next: (data) => {
        this.updateCharts(data.cpuUsage, data.memoryUsage, data.ioUsage);
      },
      error: (err) => {
        console.error('Error fetching metrics:', err);
      }
    });
  }
  fetchAwrReport() {
    this.awrService.getAWRReport().pipe(takeUntil(this.destroy$)).subscribe({
        next: (data) => {
          this.awrReport = data;
        },
        error: (err) => {
          console.error('Error fetching AWR Report', err);
        }
      }

    )
  }

  fetchAshReport() {
    this.ashService.getASHReport().pipe(takeUntil(this.destroy$)).subscribe({
        next: (data) => {
          this.ashReport = data;
        },
        error: (err) => {
          console.error('Error fetching ASH Report', err);
        }
      }

    )
  }
  createCharts() {
    this.cpuChart = this.createChart('cpuChart', 'CPU Usage');
    this.memoryChart = this.createChart('memoryChart', 'Memory Usage');
    this.ioChart = this.createChart('ioChart', 'IO Usage');
  }

  createChart(canvasId: string, label: string) {
    const chartConfig: ChartConfiguration = {
      type: 'bar' as ChartType,
      data: {
        labels: [label],
        datasets: [
          {
            label: label,
            data: [0], // Start with 0 data
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    };
    return new Chart(canvasId, chartConfig);
  }


  updateCharts(cpuUsage: number, memoryUsage: number, ioUsage: number) {
    this.cpuChart.data.datasets[0].data = [cpuUsage];
    this.cpuChart.update();

    this.memoryChart.data.datasets[0].data = [memoryUsage];
    this.memoryChart.update();


    this.ioChart.data.datasets[0].data = [ioUsage];
    this.ioChart.update();
  }

  downloadAwrReport() {
    this.generateAwrPdf(this.awrReport);
  }

  downloadAshReport() {
    this.generateAshPdf(this.ashReport);
  }

  private generateAwrPdf(awrReports: any[]): void {
    const pdf = new jsPDF('p', 'mm', 'a4');
    let yPosition = 10;
    const lineHeight = 7;
    const margin = 10;
    const pageWidth = pdf.internal.pageSize.getWidth() - 2* margin;
    const colWidth = pageWidth / 8;

    pdf.setFont('helvetica', 'bold');
    pdf.setFontSize(16);
    pdf.text('AWR Report', margin, yPosition);
    yPosition += lineHeight + 5;

    for (const awr of awrReports) {
      pdf.setFont('helvetica', 'bold');
      pdf.setFontSize(12);
      pdf.text(`Snapshot: ${awr.snapshotId}`, margin, yPosition);
      yPosition += lineHeight;

      pdf.setFont('helvetica', 'normal');
      pdf.setFontSize(10);
      pdf.text(`Start Time: ${awr.startTime}`, margin, yPosition);
      yPosition += lineHeight;
      pdf.text(`End Time: ${awr.endTime}`, margin, yPosition);
      yPosition += lineHeight +5;

      let x = margin;
      pdf.setFont('helvetica', 'bold');
      pdf.setFontSize(8);
      let headers = ["SQL ID",  "Executions", "Elapsed Time", "CPU Time", "Buffer Gets", "Disk Reads", "Rows Processed", "Plan Hash Value"];
      for (const header of headers) {
        pdf.rect(x, yPosition, colWidth, lineHeight, 'S'); // Add cell border
        pdf.text(header, x + 1, yPosition + 5, {maxWidth: colWidth - 2 , lineHeight: 2} as any); // Add some padding
        x += colWidth;
      }
      yPosition += lineHeight;
      pdf.setFont('helvetica', 'normal');
      pdf.setFontSize(8);

      for (const stat of awr.topSQLStats) {
        let x = margin;
        let statValues = [stat.sqlId, String(stat.executions), String(stat.elapsedTime), String(stat.cpuTime), String(stat.bufferGets), String(stat.diskReads), String(stat.rowsProcessed), String(stat.planHashValue)];
        for (const statValue of statValues) {
          if(statValue != null)
            pdf.rect(x, yPosition, colWidth, lineHeight, 'S'); // Add cell border
          pdf.text(String(statValue), x + 1, yPosition + 5, {maxWidth: colWidth - 2 , lineHeight: 2} as any); // Add some padding
          x += colWidth;
        }

        yPosition += lineHeight;

        if(yPosition > pdf.internal.pageSize.getHeight() - 20){
          pdf.addPage();
          yPosition = 10;
        }
      }
      yPosition += lineHeight + 5;
      if(yPosition > pdf.internal.pageSize.getHeight() - 20){
        pdf.addPage();
        yPosition = 10;
      }
    }

    pdf.save('awr_report.pdf');
  }
  private generateAshPdf(ashReports: any[]): void {
    const pdf = new jsPDF('p', 'mm', 'a4');
    let yPosition = 10;
    const lineHeight = 7;
    const margin = 10;
    const pageWidth = pdf.internal.pageSize.getWidth() - 2* margin;
    const colWidth = pageWidth / 4;

    pdf.setFont('helvetica', 'bold');
    pdf.setFontSize(16);
    pdf.text('ASH Report', margin, yPosition);
    yPosition += lineHeight + 5;
    let x = margin;
    pdf.setFont('helvetica', 'bold');
    pdf.setFontSize(10);
    let headers = ["Session ID", "Event", "Wait Class", "Sample Time"];
    for (const header of headers) {
      pdf.rect(x, yPosition, colWidth, lineHeight, 'S'); // Add cell border
      pdf.text(header, x + 1, yPosition + 5, {maxWidth: colWidth - 2, lineHeight: 2} as any); // Add some padding
      x += colWidth;
    }
    yPosition += lineHeight;
    pdf.setFont('helvetica', 'normal');
    pdf.setFontSize(8);
    for(const ash of ashReports){
      let x = margin;
      let ashValues = [ash.sessionId, ash.event, ash.waitClass, ash.sampleTime];
      for (const ashValue of ashValues) {
        if(ashValue != null)
          pdf.rect(x, yPosition, colWidth, lineHeight, 'S'); // Add cell border
        pdf.text(String(ashValue), x + 1, yPosition + 5,{maxWidth: colWidth -2,lineHeight: 2} as any); // Add some padding
        x += colWidth;
      }
      yPosition += lineHeight;

      if(yPosition > pdf.internal.pageSize.getHeight() - 20){
        pdf.addPage();
        yPosition = 10;
      }
    }
    pdf.save('ash_report.pdf');
  }


  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
