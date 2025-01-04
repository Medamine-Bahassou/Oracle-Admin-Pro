import { Component, OnInit, OnDestroy } from '@angular/core';
import { Chart, ChartConfiguration, ChartType } from 'chart.js/auto';
import { PerformanceService } from '../../services/performance/performance.service';
import { Subject, takeUntil } from 'rxjs';
import { AwrService } from '../../services/performance/awr.service';
import { AshService } from '../../services/performance/ash.service';
import jsPDF from 'jspdf';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit, OnDestroy {
  cpuChart: any;
  memoryChart: any;
  ioChart: any;
  awrChart: any;
  ashChart: any;
  awrReport: any[] = [];
  ashReport: any[] = [];
  private destroy$ = new Subject<void>();

  // Pagination for AWR Report
  currentPageAwr = 0;
  itemsPerPageAwr = 10;
  pagedAwrReport: any[] = [];
  totalPagesAwr: number = 0;
  awrTableData: any[] = []; // to hold formatted data for the table

  // Pagination for ASH Report
  currentPageAsh = 0;
  itemsPerPageAsh = 10; // Set to 10 rows per page
  pagedAshReport: any[] = [];
  totalPagesAsh: number = 0;

  sqlIdFilter: string = '';
  eventFilter: string = '';

  constructor(
    private performanceService: PerformanceService,
    private awrService: AwrService,
    private ashService: AshService
  ) {
  }

  ngOnInit() {
    this.createCharts();
    this.fetchMetrics();
    this.fetchAwrReport();
    this.fetchAshReport();
    this.fetchAwrChartData();
    this.fetchASHChartData();
    setInterval(() => {
      this.fetchMetrics();
      this.fetchAwrChartData();
      this.fetchASHChartData();
    }, 60000);
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
          this.formatAwrTableData();
          this.setPagedAwrReport();
        },
        error: (err) => {
          console.error('Error fetching AWR Report', err);
        }
      }
    );
  }

  fetchAshReport() {
    this.ashService.getASHReport().pipe(takeUntil(this.destroy$)).subscribe({
        next: (data) => {
          this.ashReport = data;
          this.setPagedAshReport();
        },
        error: (err) => {
          console.error('Error fetching ASH Report', err);
        }
      }
    );
  }


  createCharts() {
    this.cpuChart = this.createChart('cpuChart', 'CPU Usage');
    this.memoryChart = this.createChart('memoryChart', 'Memory Usage');
    this.ioChart = this.createChart('ioChart', 'IO Usage');
    this.awrChart = this.createChart('awrChart', 'AWR Report (Elapsed Time)', 'line');
    this.ashChart = this.createChart('ashChart', 'ASH Report (Event Distribution)', 'pie');
  }

  createChart(canvasId: string, label: string, type: ChartType = 'bar') {
    const chartConfig: ChartConfiguration = {
      type: type,
      data: {
        labels: type === 'bar' ? [label] : [],
        datasets: type === 'pie' ? [] : [{
          label: label,
          data: [],
          backgroundColor: 'rgba(75, 192, 192, 0.2)',
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 1,
        }],
      },
      options: {
        responsive: true,
        scales: type === 'bar' ? {
          y: {
            beginAtZero: true
          }
        } : {},

      },
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
  fetchAwrChartData() {
    this.awrService.getAWRChartData(this.sqlIdFilter).pipe(takeUntil(this.destroy$)).subscribe({
        next: (data) => {
          this.updateAWRChart(data);
        },
        error: (err) => {
          console.error('Error fetching AWR chart data:', err);
        }
      }
    );
  }
  fetchASHChartData() {
    this.ashService.getASHChartData(this.eventFilter).pipe(takeUntil(this.destroy$)).subscribe({
        next: (data) => {
          console.log('ASH Chart Data:', data); // Log received data
          this.updateASHChart(data);
        },
        error: (err) => {
          console.error('Error fetching ASH chart data:', err);
        }
      }
    );
  }

  updateASHChart(data: any) {
    console.log('Data for ASH chart:', data); // Add log here too
    const labels = Object.keys(data);
    const backgroundColors = [
      'rgba(255, 99, 132, 0.2)',
      'rgba(255, 159, 64, 0.2)',
      'rgba(255, 205, 86, 0.2)',
      'rgba(75, 192, 192, 0.2)',
      'rgba(54, 162, 235, 0.2)',
      'rgba(153, 102, 255, 0.2)',
      'rgba(201, 203, 207, 0.2)'
    ];

    const dataset = {
      label: "Event Count",
      data: Object.values(data),
      backgroundColor: labels.map((_, index) => backgroundColors[index % backgroundColors.length]),
      borderColor:  labels.map((_, index) => backgroundColors[index % backgroundColors.length].slice(0, -4) + "1)"),
      borderWidth: 1,
    };
    this.ashChart.data.labels = labels;
    this.ashChart.data.datasets = [dataset];
    this.ashChart.update();
  }

  updateAWRChart(data: any) {
    const labels = Object.keys(data);
    const datasets = labels.map((label, index) => ({
      label: label,
      data: data[label].map((item:any) => item.elapsedTime),
      backgroundColor: `rgba(${index*30}, 162, 235, 0.2)`,
      borderColor: `rgba(${index*30}, 162, 235, 1)`,
      borderWidth: 1,
    }));
    this.awrChart.data.labels =  Array.from({ length: data[Object.keys(data)[0]]?.length || 0 }, (_, i) => `SQL ${i + 1}`);
    this.awrChart.data.datasets = datasets;
    this.awrChart.update();
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
    const pageWidth = pdf.internal.pageSize.getWidth() - 2 * margin;
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
      yPosition += lineHeight + 5;

      let x = margin;
      pdf.setFont('helvetica', 'bold');
      pdf.setFontSize(8);
      let headers = ['SQL ID', 'Executions', 'Elapsed Time', 'CPU Time', 'Buffer Gets', 'Disk Reads', 'Rows Processed', 'Plan Hash Value'];
      for (const header of headers) {
        pdf.rect(x, yPosition, colWidth, lineHeight, 'S'); // Add cell border
        pdf.text(header, x + 1, yPosition + 5, {maxWidth: colWidth - 2, lineHeight: 2} as any); // Add some padding
        x += colWidth;
      }
      yPosition += lineHeight;
      pdf.setFont('helvetica', 'normal');
      pdf.setFontSize(8);

      for (const stat of awr.topSQLStats) {
        let x = margin;
        let statValues = [stat.sqlId, String(stat.executions), String(stat.elapsedTime), String(stat.cpuTime), String(stat.bufferGets), String(stat.diskReads), String(stat.rowsProcessed), String(stat.planHashValue)];
        for (const statValue of statValues) {
          if (statValue != null)
            pdf.rect(x, yPosition, colWidth, lineHeight, 'S'); // Add cell border
          pdf.text(String(statValue), x + 1, yPosition + 5, {maxWidth: colWidth - 2, lineHeight: 2} as any); // Add some padding
          x += colWidth;
        }

        yPosition += lineHeight;

        if (yPosition > pdf.internal.pageSize.getHeight() - 20) {
          pdf.addPage();
          yPosition = 10;
        }
      }
      yPosition += lineHeight + 5;
      if (yPosition > pdf.internal.pageSize.getHeight() - 20) {
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
    const pageWidth = pdf.internal.pageSize.getWidth() - 2 * margin;
    const colWidth = pageWidth / 4;

    pdf.setFont('helvetica', 'bold');
    pdf.setFontSize(16);
    pdf.text('ASH Report', margin, yPosition);
    yPosition += lineHeight + 5;
    let x = margin;
    pdf.setFont('helvetica', 'bold');
    pdf.setFontSize(10);
    let headers = ['Session ID', 'Event', 'Wait Class', 'Sample Time'];
    for (const header of headers) {
      pdf.rect(x, yPosition, colWidth, lineHeight, 'S'); // Add cell border
      pdf.text(header, x + 1, yPosition + 5, {maxWidth: colWidth - 2, lineHeight: 2} as any); // Add some padding
      x += colWidth;
    }
    yPosition += lineHeight;
    pdf.setFont('helvetica', 'normal');
    pdf.setFontSize(8);
    for (const ash of ashReports) {
      let x = margin;
      let ashValues = [ash.sessionId, ash.event, ash.waitClass, ash.sampleTime];
      for (const ashValue of ashValues) {
        if (ashValue != null)
          pdf.rect(x, yPosition, colWidth, lineHeight, 'S'); // Add cell border
        pdf.text(String(ashValue), x + 1, yPosition + 5, {maxWidth: colWidth - 2, lineHeight: 2} as any); // Add some padding
        x += colWidth;
      }
      yPosition += lineHeight;

      if (yPosition > pdf.internal.pageSize.getHeight() - 20) {
        pdf.addPage();
        yPosition = 10;
      }
    }
    pdf.save('ash_report.pdf');
  }

  formatAwrTableData() {
    this.awrTableData = [];
    for (const awr of this.awrReport) {
      for (const stat of awr.topSQLStats) {
        this.awrTableData.push({
          snapshotId: awr.snapshotId,
          startTime: awr.startTime,
          endTime: awr.endTime,
          sqlId: stat.sqlId,
          executions: stat.executions,
          elapsedTime: stat.elapsedTime,
          cpuTime: stat.cpuTime,
          bufferGets: stat.bufferGets,
          diskReads: stat.diskReads,
          rowsProcessed: stat.rowsProcessed,
          planHashValue: stat.planHashValue
        });
      }
    }
  }

  setPagedAwrReport() {
    const startIndex = this.currentPageAwr * this.itemsPerPageAwr;
    this.pagedAwrReport = this.awrTableData.slice(startIndex, startIndex + this.itemsPerPageAwr);
    this.totalPagesAwr = Math.ceil(this.awrTableData.length / this.itemsPerPageAwr);
  }


  nextPageAwr() {
    if (this.currentPageAwr < this.totalPagesAwr - 1) {
      this.currentPageAwr++;
      this.setPagedAwrReport();
    }

  }

  previousPageAwr() {
    if (this.currentPageAwr > 0) {
      this.currentPageAwr--;
      this.setPagedAwrReport();
    }
  }


  setPagedAshReport() {
    const startIndex = this.currentPageAsh * this.itemsPerPageAsh;
    this.pagedAshReport = this.ashReport.slice(startIndex, startIndex + this.itemsPerPageAsh);
    this.totalPagesAsh = Math.ceil(this.ashReport.length / this.itemsPerPageAsh);
  }


  nextPageAsh() {
    if (this.currentPageAsh < this.totalPagesAsh - 1) {
      this.currentPageAsh++;
      this.setPagedAshReport();
    }

  }

  previousPageAsh() {
    if (this.currentPageAsh > 0) {
      this.currentPageAsh--;
      this.setPagedAshReport();
    }
  }


  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
