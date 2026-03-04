import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TimeUtilService {

  private readonly timeZone = 'Africa/Addis_Ababa';

  private validate(value: string | Date | null | undefined): Date {
    if (!value) {
      throw new Error('instant must not be null');
    }

    const date = value instanceof Date ? value : new Date(value);

    if (isNaN(date.getTime())) {
      throw new Error('Invalid date');
    }

    return date;
  }

  // yyyy-MM-dd HH:mm
  formatToMinute(value: string | Date): string {
    const date = this.validate(value);

    return new Intl.DateTimeFormat('en-CA', {
      timeZone: this.timeZone,
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      hourCycle: 'h23'
    }).format(date).replace(',', '');
  }

  // yyyy-MM-dd HH:mm:ss
  formatToSecond(value: string | Date): string {
    const date = this.validate(value);

    return new Intl.DateTimeFormat('en-CA', {
      timeZone: this.timeZone,
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hourCycle: 'h23'
    }).format(date).replace(',', '');
  }

  // yyyy, MMM dd hh:mm:ss a
  formatWithMonthName(value: string | Date): string {
    const date = this.validate(value);

    return new Intl.DateTimeFormat('en-US', {
      timeZone: this.timeZone,
      year: 'numeric',
      month: 'short',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: true
    }).format(date);
  }
}