import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Chart } from 'chart.js/auto';
import { AuthService } from 'src/app/auth/services/auth.service';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';
import { ReportsDTO } from '../../models/reports-dto';
import { RideService } from '../../services/ride.service';

@Component({
  selector: 'app-user-reports',
  templateUrl: './user-reports.component.html',
  styleUrls: ['./user-reports.component.css'],
})
export class UserReportsComponent implements OnInit {
  isRequested: boolean = true;
  isAdmin: boolean = true;
  rideCountChart: any;
  kmCountChart: any;
  priceCountChart: any;
  reports!: ReportsDTO;
  dates!: string[];

  ridesSum!: number;
  rideAvg!: number;

  kmSum!: number;
  kmAvg!: number;

  priceSum!: number;
  priceAvg!: number;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private rideService: RideService,
    private popUpService: PopUpService
  ) {}

  reportForm = this.formBuilder.group({
    from: [, Validators.required],
    till: [, Validators.required],
    email: [''],
    all: [false],
  });

  ngOnInit(): void {
    this.isAdmin = this.authService.getRole() === 'ROLE_ADMIN' ? true : false;
  }

  getReport() {
    if (!this.reportForm.valid) {
      this.popUpService.showPopUp('Enter both dates');
      return;
    }

    if (!this.isDatesValid()) {
      this.popUpService.showPopUp('Dates not valid');
      return;
    }

    if (this.reportForm.value.all!) {
      this.rideService
        .getAllReports(this.getFromDate(), this.getTillDate())
        .subscribe({
          next: (response) => {
            this.reports = response;
            this.parseResponse(response);
            this.isRequested = false;
          },
          error: () => {
            this.popUpService.showPopUp('No records');
          },
        });
    } else if (this.isAdmin) {
      if (this.reportForm.value.email! === '') {
        this.popUpService.showPopUp('Enter user email');
        return;
      }

      this.rideService
        .getUserReports(
          this.reportForm.value.email!,
          this.getFromDate(),
          this.getTillDate()
        )
        .subscribe({
          next: (response) => {
            this.reports = response;
            this.parseResponse(response);
            this.isRequested = false;
          },
          error: () => {
            this.popUpService.showPopUp('No records');
          },
        });
    } else if (this.authService.getRole() === 'ROLE_PASSENGER') {
      this.rideService
        .getPassengerReports(
          this.authService.getId(),
          this.getFromDate(),
          this.getTillDate()
        )
        .subscribe({
          next: (response) => {
            this.reports = response;
            this.parseResponse(response);
            this.isRequested = false;
          },
          error: () => {
            this.popUpService.showPopUp('No records');
          },
        });
    } else if (this.authService.getRole() === 'ROLE_DRIVER') {
      this.rideService
        .getDriverReports(
          this.authService.getId(),
          this.getFromDate(),
          this.getTillDate()
        )
        .subscribe({
          next: (response) => {
            this.reports = response;
            this.parseResponse(response);
            this.isRequested = false;
          },
          error: () => {
            this.popUpService.showPopUp('No records');
          },
        });
    }
  }

  parseResponse(reports: ReportsDTO) {
    let dates: string[] = [];
    let rideCounts: number[] = [];
    let kmCounts: number[] = [];
    let priceCounts: number[] = [];
    for (let i = 0; i < reports.reports[0].days.length; i++) {
      dates.push(reports.reports[0].days[i].date);
      rideCounts.push(reports.reports[0].days[i].value);
      kmCounts.push(reports.reports[1].days[i].value);
      priceCounts.push(reports.reports[2].days[i].value);
    }

    this.ridesSum =
      Math.round(reports.reports[0].sum * 100 + Number.EPSILON) / 100;
    this.rideAvg =
      Math.round(reports.reports[0].avg * 100 + Number.EPSILON) / 100;

    this.kmSum =
      Math.round(reports.reports[1].sum * 100 + Number.EPSILON) / 100;
    this.kmAvg =
      Math.round(reports.reports[1].avg * 100 + Number.EPSILON) / 100;

    this.priceSum =
      Math.round(reports.reports[2].sum * 100 + Number.EPSILON) / 100;
    this.priceAvg =
      Math.round(reports.reports[2].avg * 100 + Number.EPSILON) / 100;

    if (this.rideCountChart != undefined) {
      this.rideCountChart.destroy();
    }

    this.rideCountChart = this.createChart(
      'rideCountChart',
      reports.reports[0].title,
      reports.reports[0].label,
      dates,
      rideCounts
    );

    if (this.kmCountChart != undefined) {
      this.kmCountChart.destroy();
    }

    this.kmCountChart = this.createChart(
      'kmCountChart',
      reports.reports[1].title,
      reports.reports[1].label,
      dates,
      kmCounts
    );

    if (this.priceCountChart != undefined) {
      this.priceCountChart.destroy();
    }

    this.priceCountChart = this.createChart(
      'priceCountChart',
      reports.reports[2].title,
      reports.reports[2].label,
      dates,
      priceCounts
    );
  }

  isDatesValid(): boolean {
    if (new Date(this.getFromDate()) < new Date(this.getTillDate())) {
      return true;
    }
    return false;
  }

  getFromDate(): string {
    return this.reportForm.value.from!;
  }

  getTillDate(): string {
    return this.reportForm.value.till!;
  }

  createChart(
    chartName: string,
    title: string,
    label: string,
    dates: string[],
    values: number[]
  ): Chart {
    return new Chart(chartName, {
      type: 'line', //this denotes tha type of chart

      data: {
        // values on X-Axis
        labels: dates,
        datasets: [
          {
            label: label,
            data: values,
            backgroundColor: '#ffbf00',
          },
        ],
      },
      options: {
        aspectRatio: 2.5,
        plugins: {
          title: {
            display: true,
            text: title,
            padding: {
              top: 10,
              bottom: 30,
            },
            color: '#333333',
            font: {
              size: 24,
            },
          },
        },
      },
    });
  }

  generatePDF() {
    print();
  }
}
